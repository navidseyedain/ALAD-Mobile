package com.alad.app.core.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.alad.app.core.audio.AudioCaptureManager
import com.alad.app.core.audio.AudioPlayerManager
import com.alad.app.core.audio.AudioUtils
import com.alad.app.core.network.ALADWebSocketManager
import com.alad.app.data.repository.DubbingSessionRepository
import com.alad.app.data.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class AudioDubbingForegroundService : Service() {

    companion object {
        private const val CHANNEL_ID = "alad_dubbing_channel"
        private const val NOTIFICATION_ID = 101

        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE"
        const val EXTRA_RESULT_DATA = "EXTRA_RESULT_DATA"
        private const val TAG = "DubbingService"

        // Delegated to DubbingSessionRepository for backward compatibility
        val isRunning: StateFlow<Boolean> get() = DubbingSessionRepository.isDubbingRunning
        val audioAmplitude: StateFlow<Float> get() = DubbingSessionRepository.audioAmplitude
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Unhandled exception in serviceScope", throwable)
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob() + coroutineExceptionHandler)

    private var mediaProjection: MediaProjection? = null
    private var audioCaptureManager: AudioCaptureManager? = null
    private var audioPlayerManager: AudioPlayerManager? = null
    private var webSocketManager: ALADWebSocketManager? = null
    private var settingsObservationJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        when (action) {
            ACTION_START -> {
                val notification = createNotification()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION or ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                    } else {
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                    }
                    startForeground(NOTIFICATION_ID, notification, serviceType)
                } else {
                    startForeground(NOTIFICATION_ID, notification)
                }

                val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0)
                val data = intent.getParcelableExtra<Intent>(EXTRA_RESULT_DATA)

                if (resultCode != 0 && data != null) {
                    startDubbing(resultCode, data)
                }
            }
            ACTION_STOP -> {
                stopDubbing()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private fun startDubbing(resultCode: Int, data: Intent) {
        DubbingSessionRepository.setDubbingRunning(true)
        DubbingSessionRepository.setConnectionStatus("Initializing...")
        DubbingSessionRepository.clearSubtitle()

        val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val projection = projectionManager.getMediaProjection(resultCode, data)
        if (projection == null) {
            Log.e(TAG, "Failed to get MediaProjection! ResultCode: $resultCode")
            DubbingSessionRepository.setConnectionStatus("Capture Denied")
            DubbingSessionRepository.setDubbingRunning(false)
            return
        }

        // CRITICAL FOR ANDROID 14 (API 34): Register MediaProjection.Callback before starting capture
        val projectionCallback = object : MediaProjection.Callback() {
            override fun onStop() {
                Log.d(TAG, "MediaProjection stopped by system")
                serviceScope.launch(Dispatchers.Main) {
                    try {
                        stopDubbing()
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error stopping dubbing from projection callback", e)
                    }
                }
            }
        }
        projection.registerCallback(projectionCallback, Handler(Looper.getMainLooper()))
        mediaProjection = projection

        serviceScope.launch {
            try {
                val repository = UserPreferencesRepository(applicationContext)

                val apiKey = repository.apiKeyFlow.first()
                val targetLang = repository.targetLangFlow.first()
                val targetDialect = repository.targetDialectFlow.first()
                val volumeRatio = repository.volumeRatioFlow.first()
                val isDubbingMuted = repository.isDubbingMutedFlow.first()
                val originalVolume = repository.originalVolumeFlow.first()
                val isOriginalMuted = repository.isOriginalMutedFlow.first()
                val voicePersona = repository.voicePersonaFlow.first()
                val dubbingMode = repository.dubbingModeFlow.first()
                val showDualSubtitles = repository.showDualSubtitlesFlow.first()

                // Sync repository values into the active session
                DubbingSessionRepository.setDubbingVolume(volumeRatio)
                DubbingSessionRepository.setDubbingMuted(isDubbingMuted)
                DubbingSessionRepository.setOriginalVolumeRatio(originalVolume)
                DubbingSessionRepository.setOriginalMuted(isOriginalMuted)
                DubbingSessionRepository.setShowDualSubtitles(showDualSubtitles)

                val okHttpClient = OkHttpClient.Builder()
                    .pingInterval(15, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build()

                webSocketManager = ALADWebSocketManager(okHttpClient)

                // Initialize AudioPlayerManager if not in SUBTITLE_ONLY mode
                if (dubbingMode != UserPreferencesRepository.MODE_SUBTITLE_ONLY) {
                    audioPlayerManager = AudioPlayerManager(applicationContext)
                    audioPlayerManager?.start()
                    audioPlayerManager?.setDubbingAudio(volumeRatio, isDubbingMuted)
                    audioPlayerManager?.setOriginalMediaAudio(originalVolume, isOriginalMuted)
                }

                webSocketManager?.onStatusChanged = { status ->
                    DubbingSessionRepository.setConnectionStatus(status)
                }

                webSocketManager?.onTextMessageReceived = { textDelta ->
                    DubbingSessionRepository.appendSubtitleText(textDelta)
                }

                webSocketManager?.onSourceTextReceived = { sourceChunk ->
                    DubbingSessionRepository.appendSourceSubtitleText(sourceChunk)
                }

                webSocketManager?.onTurnComplete = {
                    DubbingSessionRepository.onTurnComplete()
                }

                webSocketManager?.onBinaryMessageReceived = { audioChunk ->
                    if (dubbingMode != UserPreferencesRepository.MODE_SUBTITLE_ONLY) {
                        audioPlayerManager?.playAudioData(audioChunk)
                    }
                }

                webSocketManager?.connect(
                    apiKey = apiKey,
                    sourceLang = "",
                    targetLang = targetLang,
                    voicePersona = voicePersona,
                    dubbingMode = dubbingMode,
                    targetDialect = targetDialect
                )

                // Dynamic observation for audio controls and settings
                settingsObservationJob?.cancel()
                settingsObservationJob = serviceScope.launch {
                    // 1. Reactive dubbing volume updates
                    launch {
                        kotlinx.coroutines.flow.combine(
                            DubbingSessionRepository.dubbingVolume,
                            DubbingSessionRepository.isDubbingMuted
                        ) { vol, muted -> Pair(vol, muted) }.collect { (vol, muted) ->
                            audioPlayerManager?.setDubbingAudio(vol, muted)
                        }
                    }

                    // 2. Reactive original media volume & ducking updates
                    launch {
                        kotlinx.coroutines.flow.combine(
                            DubbingSessionRepository.originalVolumeRatio,
                            DubbingSessionRepository.isOriginalMuted
                        ) { ratio, muted -> Pair(ratio, muted) }.collect { (ratio, muted) ->
                            audioPlayerManager?.setOriginalMediaAudio(ratio, muted)
                        }
                    }

                    // 3. Dynamic observation for language/dialect/voice changes during session
                    launch {
                        var firstEmit = true
                        kotlinx.coroutines.flow.combine(
                            repository.targetLangFlow,
                            repository.targetDialectFlow,
                            repository.voicePersonaFlow
                        ) { lang, dialect, voice -> Triple(lang, dialect, voice) }.collect { (newLang, newDialect, newVoice) ->
                            if (firstEmit) {
                                firstEmit = false
                            } else {
                                val currentKey = repository.apiKeyFlow.first()
                                val currentMode = repository.dubbingModeFlow.first()
                                webSocketManager?.disconnect()
                                webSocketManager?.connect(
                                    apiKey = currentKey,
                                    sourceLang = "",
                                    targetLang = newLang,
                                    voicePersona = newVoice,
                                    dubbingMode = currentMode,
                                    targetDialect = newDialect
                                )
                            }
                        }
                    }
                }

                audioCaptureManager = AudioCaptureManager()
                val appUid = applicationInfo.uid

                mediaProjection?.let { projection ->
                    audioCaptureManager?.startCapture(projection, appUid) { pcmData ->
                        webSocketManager?.sendAudioData(pcmData)

                        // Process RMS and smoothed amplitude via decoupled AudioUtils
                        val currentAmp = DubbingSessionRepository.audioAmplitude.value
                        val newAmp = AudioUtils.processPcmAmplitude(pcmData, currentAmp)
                        DubbingSessionRepository.setAudioAmplitude(newAmp)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during dubbing session startup", e)
                DubbingSessionRepository.setConnectionStatus("Error: ${e.message}")
            }
        }
    }

    private fun stopDubbing() {
        DubbingSessionRepository.resetSession()

        audioCaptureManager?.stopCapture()
        audioCaptureManager = null

        audioPlayerManager?.stop()
        audioPlayerManager = null

        webSocketManager?.disconnect()
        webSocketManager = null

        settingsObservationJob?.cancel()
        settingsObservationJob = null

        mediaProjection?.stop()
        mediaProjection = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (DubbingSessionRepository.isDubbingRunning.value) {
            stopDubbing()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Live Dubbing Service"
            val descriptionText = "Capturing and streaming audio for dubbing"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, com.alad.app.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            this, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, AudioDubbingForegroundService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = android.app.PendingIntent.getService(
            this, 1, stopIntent, android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ALAD Live Dubbing")
            .setContentText("Connected and dubbing...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Disconnect", stopPendingIntent)
            .setOngoing(true)
            .build()
        }
}
