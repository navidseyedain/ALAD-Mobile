package com.alad.app.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alad.app.data.repository.DubbingSessionRepository
import com.alad.app.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserPreferencesRepository) : ViewModel() {

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
        viewModelScope.launch { repository.updateSourceLang(lang) }
    }

    fun updateTargetLang(lang: String) {
        viewModelScope.launch { repository.updateTargetLang(lang) }
    }

    fun updateDubbingMode(mode: String) {
        viewModelScope.launch { repository.updateDubbingMode(mode) }
    }

    fun updateVoicePersona(voice: String) {
        viewModelScope.launch { repository.updateVoicePersona(voice) }
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
