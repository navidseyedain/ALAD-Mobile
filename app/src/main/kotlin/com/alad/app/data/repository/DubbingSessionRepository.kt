package com.alad.app.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Single source of truth for the real-time dubbing session state.
 * Decouples the Android Service lifecycle from UI components (MainScreen, OverlayWidget).
 */
object DubbingSessionRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var silenceJob: Job? = null
    private var isNewTurn = true
    private var isNewSourceTurn = true

    private val _isDubbingRunning = MutableStateFlow(false)
    val isDubbingRunning: StateFlow<Boolean> = _isDubbingRunning.asStateFlow()

    private val _audioAmplitude = MutableStateFlow(0f)
    val audioAmplitude: StateFlow<Float> = _audioAmplitude.asStateFlow()

    private val _connectionStatus = MutableStateFlow("Disconnected")
    val connectionStatus: StateFlow<String> = _connectionStatus.asStateFlow()

    // Live translated subtitle text
    private val _liveSubtitleText = MutableStateFlow("")
    val liveSubtitleText: StateFlow<String> = _liveSubtitleText.asStateFlow()

    // Live source audio transcript text (for dual subtitle display)
    private val _liveSourceSubtitleText = MutableStateFlow("")
    val liveSourceSubtitleText: StateFlow<String> = _liveSourceSubtitleText.asStateFlow()

    private val _isSubtitleOverlayVisible = MutableStateFlow(true)
    val isSubtitleOverlayVisible: StateFlow<Boolean> = _isSubtitleOverlayVisible.asStateFlow()

    // Real-time audio mixer session controls
    private val _dubbingVolume = MutableStateFlow(1.0f)
    val dubbingVolume: StateFlow<Float> = _dubbingVolume.asStateFlow()

    private val _isDubbingMuted = MutableStateFlow(false)
    val isDubbingMuted: StateFlow<Boolean> = _isDubbingMuted.asStateFlow()

    private val _originalVolumeRatio = MutableStateFlow(0.30f)
    val originalVolumeRatio: StateFlow<Float> = _originalVolumeRatio.asStateFlow()

    private val _isOriginalMuted = MutableStateFlow(false)
    val isOriginalMuted: StateFlow<Boolean> = _isOriginalMuted.asStateFlow()

    private val _showDualSubtitles = MutableStateFlow(true)
    val showDualSubtitles: StateFlow<Boolean> = _showDualSubtitles.asStateFlow()

    fun setDubbingRunning(running: Boolean) {
        _isDubbingRunning.value = running
        if (!running) {
            _audioAmplitude.value = 0f
            _connectionStatus.value = "Disconnected"
            clearSubtitle()
        }
    }

    fun setAudioAmplitude(amplitude: Float) {
        _audioAmplitude.value = amplitude
    }

    fun setConnectionStatus(status: String) {
        _connectionStatus.value = status
    }

    fun appendSubtitleText(textChunk: String) {
        val trimmed = textChunk.trim()
        if (trimmed.isEmpty()) return

        silenceJob?.cancel()

        if (isNewTurn) {
            isNewTurn = false
            _liveSubtitleText.value = trimmed
            return
        }

        val current = _liveSubtitleText.value
        val updated = if (current.isEmpty()) {
            trimmed
        } else if (trimmed.startsWith(current, ignoreCase = true)) {
            trimmed
        } else if (current.startsWith(trimmed, ignoreCase = true) || current.endsWith(trimmed, ignoreCase = true)) {
            current
        } else {
            if (current.endsWith(" ") || trimmed.startsWith(" ") || trimmed.startsWith(",") || trimmed.startsWith(".")) {
                current + trimmed
            } else {
                "$current $trimmed"
            }
        }

        _liveSubtitleText.value = if (updated.length > 180) {
            val spaceIndex = updated.indexOf(' ', updated.length - 130)
            if (spaceIndex != -1) updated.substring(spaceIndex + 1) else updated.takeLast(130)
        } else {
            updated
        }
    }

    fun appendSourceSubtitleText(textChunk: String) {
        val trimmed = textChunk.trim()
        if (trimmed.isEmpty()) return

        silenceJob?.cancel()

        if (isNewSourceTurn) {
            isNewSourceTurn = false
            _liveSourceSubtitleText.value = trimmed
            return
        }

        val current = _liveSourceSubtitleText.value
        val updated = if (current.isEmpty()) {
            trimmed
        } else if (trimmed.startsWith(current, ignoreCase = true)) {
            trimmed
        } else if (current.startsWith(trimmed, ignoreCase = true) || current.endsWith(trimmed, ignoreCase = true)) {
            current
        } else {
            if (current.endsWith(" ") || trimmed.startsWith(" ") || trimmed.startsWith(",") || trimmed.startsWith(".")) {
                current + trimmed
            } else {
                "$current $trimmed"
            }
        }

        _liveSourceSubtitleText.value = if (updated.length > 180) {
            val spaceIndex = updated.indexOf(' ', updated.length - 130)
            if (spaceIndex != -1) updated.substring(spaceIndex + 1) else updated.takeLast(130)
        } else {
            updated
        }
    }

    fun onTurnComplete() {
        isNewTurn = true
        isNewSourceTurn = true
        silenceJob?.cancel()
        silenceJob = repositoryScope.launch {
            delay(4500)
            _liveSubtitleText.value = ""
            _liveSourceSubtitleText.value = ""
        }
    }

    fun clearSubtitle() {
        silenceJob?.cancel()
        _liveSubtitleText.value = ""
        _liveSourceSubtitleText.value = ""
        isNewTurn = true
        isNewSourceTurn = true
    }

    fun toggleSubtitleOverlayVisibility() {
        val newVisibility = !_isSubtitleOverlayVisible.value
        _isSubtitleOverlayVisible.value = newVisibility
        if (newVisibility) {
            clearSubtitle()
        }
    }

    fun setSubtitleOverlayVisibility(visible: Boolean) {
        _isSubtitleOverlayVisible.value = visible
        if (visible) {
            clearSubtitle()
        }
    }

    fun setDubbingVolume(volume: Float) {
        _dubbingVolume.value = volume.coerceIn(0f, 1f)
    }

    fun toggleDubbingMute() {
        _isDubbingMuted.value = !_isDubbingMuted.value
    }

    fun setDubbingMuted(muted: Boolean) {
        _isDubbingMuted.value = muted
    }

    fun setOriginalVolumeRatio(ratio: Float) {
        _originalVolumeRatio.value = ratio.coerceIn(0f, 1f)
    }

    fun toggleOriginalMute() {
        _isOriginalMuted.value = !_isOriginalMuted.value
    }

    fun setOriginalMuted(muted: Boolean) {
        _isOriginalMuted.value = muted
    }

    fun setShowDualSubtitles(show: Boolean) {
        _showDualSubtitles.value = show
    }

    fun resetSession() {
        _isDubbingRunning.value = false
        _audioAmplitude.value = 0f
        _connectionStatus.value = "Disconnected"
        clearSubtitle()
    }
}
