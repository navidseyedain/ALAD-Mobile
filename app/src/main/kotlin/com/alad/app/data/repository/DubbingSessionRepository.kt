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

    private val _isDubbingRunning = MutableStateFlow(false)
    val isDubbingRunning: StateFlow<Boolean> = _isDubbingRunning.asStateFlow()

    private val _audioAmplitude = MutableStateFlow(0f)
    val audioAmplitude: StateFlow<Float> = _audioAmplitude.asStateFlow()

    private val _connectionStatus = MutableStateFlow("Disconnected")
    val connectionStatus: StateFlow<String> = _connectionStatus.asStateFlow()

    private val _liveSubtitleText = MutableStateFlow("")
    val liveSubtitleText: StateFlow<String> = _liveSubtitleText.asStateFlow()

    private val _isSubtitleOverlayVisible = MutableStateFlow(true)
    val isSubtitleOverlayVisible: StateFlow<Boolean> = _isSubtitleOverlayVisible.asStateFlow()

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

        // Speech is actively arriving; cancel any pending silence clearance
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
            // Cumulative transcript update from model turn
            trimmed
        } else if (current.startsWith(trimmed, ignoreCase = true) || current.endsWith(trimmed, ignoreCase = true)) {
            // Duplicate emission skip
            current
        } else {
            // Incremental chunk
            if (current.endsWith(" ") || trimmed.startsWith(" ") || trimmed.startsWith(",") || trimmed.startsWith(".")) {
                current + trimmed
            } else {
                "$current $trimmed"
            }
        }

        // Keep last ~160 characters so subtitles stay fresh, readable and don't overflow the screen
        _liveSubtitleText.value = if (updated.length > 180) {
            val spaceIndex = updated.indexOf(' ', updated.length - 130)
            if (spaceIndex != -1) updated.substring(spaceIndex + 1) else updated.takeLast(130)
        } else {
            updated
        }
    }

    fun onTurnComplete() {
        isNewTurn = true
        // Keep the translated phrase visible for 4.5 seconds after turn finishes, then clear if silence continues
        silenceJob?.cancel()
        silenceJob = repositoryScope.launch {
            delay(4500)
            _liveSubtitleText.value = ""
        }
    }

    fun clearSubtitle() {
        silenceJob?.cancel()
        _liveSubtitleText.value = ""
        isNewTurn = true
    }

    fun toggleSubtitleOverlayVisibility() {
        val newVisibility = !_isSubtitleOverlayVisible.value
        _isSubtitleOverlayVisible.value = newVisibility
        if (newVisibility) {
            // Clear any stale text immediately so waiting animation displays until fresh translation arrives
            clearSubtitle()
        }
    }

    fun setSubtitleOverlayVisibility(visible: Boolean) {
        _isSubtitleOverlayVisible.value = visible
        if (visible) {
            clearSubtitle()
        }
    }

    fun resetSession() {
        _isDubbingRunning.value = false
        _audioAmplitude.value = 0f
        _connectionStatus.value = "Disconnected"
        clearSubtitle()
    }
}
