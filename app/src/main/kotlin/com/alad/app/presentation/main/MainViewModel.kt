package com.alad.app.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alad.app.data.repository.DubbingSessionRepository
import com.alad.app.data.repository.UserPreferencesRepository
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserPreferencesRepository) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("MainViewModel", "Unhandled coroutine error", throwable)
    }

    // Decoupled session state observed from DubbingSessionRepository
    val isConnected: StateFlow<Boolean> = DubbingSessionRepository.isDubbingRunning
    val audioAmplitude: StateFlow<Float> = DubbingSessionRepository.audioAmplitude
    val statusMessage: StateFlow<String> = DubbingSessionRepository.connectionStatus
    val liveSubtitleText: StateFlow<String> = DubbingSessionRepository.liveSubtitleText
    val liveSourceSubtitleText: StateFlow<String> = DubbingSessionRepository.liveSourceSubtitleText

    // Audio Mixer State
    val dubbingVolume: StateFlow<Float> = DubbingSessionRepository.dubbingVolume
    val isDubbingMuted: StateFlow<Boolean> = DubbingSessionRepository.isDubbingMuted
    val originalVolumeRatio: StateFlow<Float> = DubbingSessionRepository.originalVolumeRatio
    val isOriginalMuted: StateFlow<Boolean> = DubbingSessionRepository.isOriginalMuted
    val showDualSubtitles: StateFlow<Boolean> = DubbingSessionRepository.showDualSubtitles

    val sourceLang: StateFlow<String> = repository.sourceLangFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val targetLang: StateFlow<String> = repository.targetLangFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "fa")

    val targetDialect: StateFlow<String> = repository.targetDialectFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val dubbingMode: StateFlow<String> = repository.dubbingModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferencesRepository.MODE_HYBRID)

    val voicePersona: StateFlow<String> = repository.voicePersonaFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Puck")

    init {
        viewModelScope.launch(exceptionHandler) {
            try {
                // Initialize session repository once with saved mixer preferences
                DubbingSessionRepository.setDubbingVolume(repository.volumeRatioFlow.first())
                DubbingSessionRepository.setDubbingMuted(repository.isDubbingMutedFlow.first())
                DubbingSessionRepository.setOriginalVolumeRatio(repository.originalVolumeFlow.first())
                DubbingSessionRepository.setOriginalMuted(repository.isOriginalMutedFlow.first())
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error reading initial mixer settings", e)
            }
        }
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.showDualSubtitlesFlow.collect { show ->
                    DubbingSessionRepository.setShowDualSubtitles(show)
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error reading showDualSubtitles", e)
            }
        }
    }

    fun updateSourceLang(lang: String) {
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.updateSourceLang(lang)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to update source language", e)
            }
        }
    }

    fun updateTargetLang(lang: String) {
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.updateTargetLang(lang)
                // Clear dialect when language changes to standard
                repository.updateTargetDialect("")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to update target language", e)
            }
        }
    }

    fun updateTargetDialect(dialect: String) {
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.updateTargetDialect(dialect)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to update target dialect", e)
            }
        }
    }

    fun setDubbingVolume(vol: Float) {
        DubbingSessionRepository.setDubbingVolume(vol)
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.updateVolumeRatio(vol)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to save dubbing volume", e)
            }
        }
    }

    fun toggleDubbingMute() {
        DubbingSessionRepository.toggleDubbingMute()
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.updateDubbingMuted(DubbingSessionRepository.isDubbingMuted.value)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to save dubbing mute", e)
            }
        }
    }

    fun setOriginalVolumeRatio(ratio: Float) {
        DubbingSessionRepository.setOriginalVolumeRatio(ratio)
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.updateOriginalVolume(ratio)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to save original volume", e)
            }
        }
    }

    fun toggleOriginalMute() {
        DubbingSessionRepository.toggleOriginalMute()
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.updateOriginalMuted(DubbingSessionRepository.isOriginalMuted.value)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to save original mute", e)
            }
        }
    }

    fun updateDubbingMode(mode: String) {
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.updateDubbingMode(mode)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to update dubbing mode", e)
            }
        }
    }

    fun updateVoicePersona(voice: String) {
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.updateVoicePersona(voice)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to update voice persona", e)
            }
        }
    }

    fun updateStatus(status: String) {
        DubbingSessionRepository.setConnectionStatus(status)
    }
}

class MainViewModelFactory(private val repository: UserPreferencesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
