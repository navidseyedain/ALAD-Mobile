package com.alad.app.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alad.app.data.repository.UserPreferencesRepository
import com.alad.app.core.service.AudioDubbingForegroundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserPreferencesRepository) : ViewModel() {
    
    // Sync isConnected with the actual Service running state
    val isConnected: StateFlow<Boolean> = AudioDubbingForegroundService.isRunning
    
    private val _statusMessage = MutableStateFlow("Disconnected")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()
    
    val sourceLang: StateFlow<String> = repository.sourceLangFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")
    val targetLang: StateFlow<String> = repository.targetLangFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "fa")

    fun updateSourceLang(lang: String) {
        viewModelScope.launch { repository.updateSourceLang(lang) }
    }

    fun updateTargetLang(lang: String) {
        viewModelScope.launch { repository.updateTargetLang(lang) }
    }

    fun updateStatus(status: String) {
        _statusMessage.value = status
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
