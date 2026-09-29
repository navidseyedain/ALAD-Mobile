package com.alad.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "alad_settings")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val WS_URL = stringPreferencesKey("ws_url")
        val API_KEY = stringPreferencesKey("api_key")
        val SOURCE_LANG = stringPreferencesKey("source_lang")
        val TARGET_LANG = stringPreferencesKey("target_lang")
        val VOLUME_RATIO = floatPreferencesKey("volume_ratio")
        val VOICE_PERSONA = stringPreferencesKey("voice_persona")
        val DUBBING_MODE = stringPreferencesKey("dubbing_mode")

        const val MODE_HYBRID = "HYBRID"               // Voice dubbing + Live subtitles
        const val MODE_AUDIO_ONLY = "AUDIO_ONLY"       // Voice dubbing only
        const val MODE_SUBTITLE_ONLY = "SUBTITLE_ONLY" // Live subtitles only (silent mode)

        val AVAILABLE_VOICES = listOf(
            "Puck",    // Energetic & Friendly
            "Charon",  // Deep & Resonant
            "Aoede",   // Melodic & Warm
            "Fenrir",  // Crisp & Authoritative
            "Kore"     // Gentle & Calm
        )
    }

    val wsUrlFlow: Flow<String> = context.dataStore.data.map { it[WS_URL] ?: "ws://192.168.1.100:8000/ws/dub" }
    val apiKeyFlow: Flow<String> = context.dataStore.data.map { it[API_KEY] ?: "" }
    val sourceLangFlow: Flow<String> = context.dataStore.data.map { it[SOURCE_LANG] ?: "en" }
    val targetLangFlow: Flow<String> = context.dataStore.data.map { it[TARGET_LANG] ?: "fa" }
    val volumeRatioFlow: Flow<Float> = context.dataStore.data.map { it[VOLUME_RATIO] ?: 1.0f }
    val voicePersonaFlow: Flow<String> = context.dataStore.data.map { it[VOICE_PERSONA] ?: "Puck" }
    val dubbingModeFlow: Flow<String> = context.dataStore.data.map { it[DUBBING_MODE] ?: MODE_HYBRID }

    suspend fun updateWsUrl(url: String) { context.dataStore.edit { it[WS_URL] = url } }
    suspend fun updateApiKey(key: String) { context.dataStore.edit { it[API_KEY] = key } }
    suspend fun updateSourceLang(lang: String) { context.dataStore.edit { it[SOURCE_LANG] = lang } }
    suspend fun updateTargetLang(lang: String) { context.dataStore.edit { it[TARGET_LANG] = lang } }
    suspend fun updateVolumeRatio(ratio: Float) { context.dataStore.edit { it[VOLUME_RATIO] = ratio } }
    suspend fun updateVoicePersona(voice: String) { context.dataStore.edit { it[VOICE_PERSONA] = voice } }
    suspend fun updateDubbingMode(mode: String) { context.dataStore.edit { it[DUBBING_MODE] = mode } }
}
