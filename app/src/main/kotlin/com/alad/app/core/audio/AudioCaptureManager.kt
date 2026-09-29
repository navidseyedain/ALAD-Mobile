package com.alad.app.core.audio

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.projection.MediaProjection
import android.util.Log

class AudioCaptureManager {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    companion object {
        private const val TAG = "AudioCaptureManager"
        const val SAMPLE_RATE = 16000
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        val MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        const val CHUNK_SIZE = 3200 // 100ms of 16kHz 16-bit mono PCM (Gemini Live specification)
    }

    @SuppressLint("MissingPermission")
    fun startCapture(mediaProjection: MediaProjection, appUid: Int, onAudioData: (ByteArray) -> Unit) {
        if (isRecording) return

        try {
            val configBuilder = AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
                .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                .addMatchingUsage(AudioAttributes.USAGE_GAME)
                .addMatchingUsage(AudioAttributes.USAGE_UNKNOWN)
                
            if (appUid > 0) {
                configBuilder.excludeUid(appUid)
            }
                
            val config = configBuilder.build()

            val format = AudioFormat.Builder()
                .setEncoding(AUDIO_FORMAT)
                .setSampleRate(SAMPLE_RATE)
                .setChannelMask(CHANNEL_CONFIG)
                .build()

            val bufferSize = maxOf(MIN_BUFFER_SIZE, CHUNK_SIZE * 2)

            audioRecord = AudioRecord.Builder()
                .setAudioFormat(format)
                .setAudioPlaybackCaptureConfig(config)
                .setBufferSizeInBytes(bufferSize)
                .build()

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord failed to initialize! State: ${audioRecord?.state}")
                return
            }

            audioRecord?.startRecording()
            isRecording = true
            Log.d(TAG, "AudioPlaybackCapture started successfully. Recording state: ${audioRecord?.recordingState}")
            
            Thread({
                val buffer = ByteArray(CHUNK_SIZE)
                var chunksRead = 0
                while (isRecording) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        chunksRead++
                        if (chunksRead % 50 == 0) {
                            Log.d(TAG, "Captured 50 chunks of size $read ($CHUNK_SIZE expected)")
                        }
                        onAudioData(buffer.copyOf(read))
                    }
                }
            }, "AudioCaptureThread").start()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting capture", e)
        }
    }

    fun stopCapture() {
        isRecording = false
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping audioRecord", e)
        }
        audioRecord = null
    }
}
