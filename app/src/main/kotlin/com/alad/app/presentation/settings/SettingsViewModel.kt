package com.alad.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alad.app.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: UserPreferencesRepository) : ViewModel() {
    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _volumeRatio = MutableStateFlow(1.0f)
    val volumeRatio: StateFlow<Float> = _volumeRatio.asStateFlow()

    private val _voicePersona = MutableStateFlow("Puck")
    val voicePersona: StateFlow<String> = _voicePersona.asStateFlow()

    private val _dubbingMode = MutableStateFlow(UserPreferencesRepository.MODE_HYBRID)
    val dubbingMode: StateFlow<String> = _dubbingMode.asStateFlow()

    init {
        viewModelScope.launch {
            _apiKey.value = repository.apiKeyFlow.first()
            _volumeRatio.value = repository.volumeRatioFlow.first()
            _voicePersona.value = repository.voicePersonaFlow.first()
            _dubbingMode.value = repository.dubbingModeFlow.first()
        }
    }

    fun updateApiKey(key: String) { _apiKey.value = key }
    fun updateVolumeRatio(ratio: Float) { _volumeRatio.value = ratio }
    fun updateVoicePersona(voice: String) { _voicePersona.value = voice }
    fun updateDubbingMode(mode: String) { _dubbingMode.value = mode }

    fun saveSettings() {
        viewModelScope.launch {
            repository.updateApiKey(_apiKey.value)
            repository.updateVolumeRatio(_volumeRatio.value)
            repository.updateVoicePersona(_voicePersona.value)
            repository.updateDubbingMode(_dubbingMode.value)
        }
    }
}

class SettingsViewModelFactory(private val repository: UserPreferencesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
