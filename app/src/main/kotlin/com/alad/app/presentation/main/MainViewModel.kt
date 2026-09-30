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

    val sourceLang: StateFlow<String> = repository.sourceLangFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val targetLang: StateFlow<String> = repository.targetLangFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "fa")

    val dubbingMode: StateFlow<String> = repository.dubbingModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferencesRepository.MODE_HYBRID)

    val voicePersona: StateFlow<String> = repository.voicePersonaFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Puck")

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
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to update target language", e)
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
