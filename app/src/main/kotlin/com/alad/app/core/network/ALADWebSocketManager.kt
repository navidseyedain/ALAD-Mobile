package com.alad.app.core.network

import android.util.Base64
import android.util.Log
import okhttp3.*
import okio.ByteString
import org.json.JSONArray
import org.json.JSONObject

class ALADWebSocketManager(private val client: OkHttpClient) {
    private var webSocket: WebSocket? = null
    var onBinaryMessageReceived: ((ByteArray) -> Unit)? = null
    var onTextMessageReceived: ((String) -> Unit)? = null
    var onSourceTextReceived: ((String) -> Unit)? = null
    var onTurnComplete: (() -> Unit)? = null
    var onStatusChanged: ((String) -> Unit)? = null

    companion object {
        private const val TAG = "ALADWebSocketManager"
        private const val GEMINI_WS_URL = "wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1beta.GenerativeService.BidiGenerateContent"
    }

    private var isSetupComplete = false
    private var currentVoicePersona = "Puck"
    private var currentDubbingMode = "HYBRID"
    private var currentTargetDialect = ""

    fun connect(
        apiKey: String,
        sourceLang: String,
        targetLang: String,
        voicePersona: String = "Puck",
        dubbingMode: String = "HYBRID",
        targetDialect: String = ""
    ) {
        currentVoicePersona = voicePersona
        currentDubbingMode = dubbingMode
        currentTargetDialect = targetDialect
        val finalUrl = "$GEMINI_WS_URL?key=$apiKey"
        val request = Request.Builder().url(finalUrl).build()
        isSetupComplete = false

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "Connected to Gemini Live API")
                onStatusChanged?.invoke("Connected")
                sendGeminiSetup(targetLang, currentVoicePersona, currentDubbingMode, currentTargetDialect)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Raw message: $text")
                try {
                    val json = JSONObject(text)

                    if (json.has("serverContent") || json.has("server_content")) {
                        val serverContent = json.optJSONObject("serverContent") ?: json.optJSONObject("server_content")
                        if (serverContent != null) {
                            // 1. Output transcription: translated text for live subtitles
                            val outputTranscription = serverContent.optJSONObject("outputTranscription")
                                ?: serverContent.optJSONObject("output_transcription")
                            if (outputTranscription != null) {
                                val translatedText = outputTranscription.optString("text", "")
                                if (translatedText.isNotEmpty()) {
                                    Log.d(TAG, "Live subtitle output: $translatedText")
                                    onTextMessageReceived?.invoke(translatedText)
                                }
                            }

                            // 2. Input transcription: source speech transcript
                            val inputTranscription = serverContent.optJSONObject("inputTranscription")
                                ?: serverContent.optJSONObject("input_transcription")
                            if (inputTranscription != null) {
                                val sourceText = inputTranscription.optString("text", "")
                                if (sourceText.isNotEmpty()) {
                                    Log.d(TAG, "Source speech input: $sourceText")
                                    onSourceTextReceived?.invoke(sourceText)
                                }
                            }

                            // 3. Model turn parts: audio PCM chunks & optional text parts
                            val modelTurn = serverContent.optJSONObject("modelTurn") ?: serverContent.optJSONObject("model_turn")
                            val parts = modelTurn?.optJSONArray("parts")
                            if (parts != null) {
                                for (i in 0 until parts.length()) {
                                    val part = parts.getJSONObject(i)

                                    if (part.has("text")) {
                                        val textDelta = part.optString("text", "")
                                        if (textDelta.isNotEmpty()) {
                                            Log.d(TAG, "Part text: $textDelta")
                                            onTextMessageReceived?.invoke(textDelta)
                                        }
                                    }

                                    val inlineData = part.optJSONObject("inlineData") ?: part.optJSONObject("inline_data")
                                    if (inlineData != null) {
                                        val base64Data = inlineData.getString("data")
                                        val audioBytes = Base64.decode(base64Data, Base64.DEFAULT)
                                        onBinaryMessageReceived?.invoke(audioBytes)
                                    }
                                }
                            }

                            // 4. Check for turn completion
                            val turnComplete = serverContent.optBoolean("turnComplete", false)
                                || serverContent.optBoolean("turn_complete", false)
                            if (turnComplete) {
                                Log.d(TAG, "Turn complete from Gemini Live API")
                                onTurnComplete?.invoke()
                            }
                        }
                    } else if (json.has("setupComplete") || json.has("setup_complete")) {
                        isSetupComplete = true
                        Log.d(TAG, "Gemini Setup Complete - Ready for audio streaming")
                        onStatusChanged?.invoke("Gemini Ready")
                        pendingAudio.forEach { sendAudioNow(it) }
                        pendingAudio.clear()
                    } else if (json.has("error")) {
                        val errMessage = json.getJSONObject("error").optString("message", "Unknown error")
                        Log.e(TAG, "API Error: $errMessage")
                        onStatusChanged?.invoke("Error: $errMessage")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing message", e)
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                val text = bytes.utf8()
                onMessage(webSocket, text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                isSetupComplete = false
                Log.d(TAG, "Closing: $code $reason")
                onStatusChanged?.invoke("Disconnected: $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isSetupComplete = false
                Log.e(TAG, "WebSocket error", t)
                onStatusChanged?.invoke("Error: ${t.message}")
            }
        })
    }

    private fun sendGeminiSetup(targetLang: String, voicePersona: String, dubbingMode: String, targetDialect: String) {
        val targetLangCode = targetLang.split("-")[0]

        val dialectInstruction = com.alad.app.presentation.main.supportedDialects[targetLangCode]
            ?.find { it.code.equals(targetDialect, ignoreCase = true) }
            ?.promptInstruction ?: if (targetDialect.isNotEmpty()) "Speak and translate in $targetDialect dialect" else ""

        val setupPayload = JSONObject().apply {
            put("setup", JSONObject().apply {
                put("model", "models/gemini-3.5-live-translate-preview")
                put("generationConfig", JSONObject().apply {
                    put("responseModalities", JSONArray().put("AUDIO"))

                    put("speechConfig", JSONObject().apply {
                        put("voiceConfig", JSONObject().apply {
                            put("prebuiltVoiceConfig", JSONObject().apply {
                                put("voiceName", voicePersona)
                            })
                        })
                    })

                    put("translationConfig", JSONObject().apply {
                        put("targetLanguageCode", targetLangCode)
                        put("echoTargetLanguage", true)
                    })
                })

                if (dialectInstruction.isNotEmpty()) {
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().put(JSONObject().apply {
                            put("text", "You are an expert real-time AI live audio dubber. $dialectInstruction. Maintain natural pacing and high clarity.")
                        }))
                    })
                }

                // Direct children of BidiGenerateContentSetup according to Google Gemini Live API specification
                put("inputAudioTranscription", JSONObject())
                put("outputAudioTranscription", JSONObject())
                put("sessionResumption", JSONObject().apply {
                    put("handle", JSONObject.NULL)
                })
            })
        }
        Log.d(TAG, "Setup message payload: $setupPayload")
        webSocket?.send(setupPayload.toString())
    }

    private val pendingAudio = mutableListOf<String>()

    fun sendAudioData(pcmData: ByteArray) {
        val base64Audio = Base64.encodeToString(pcmData, Base64.NO_WRAP)
        if (!isSetupComplete) {
            pendingAudio.add(base64Audio)
            if (pendingAudio.size > 8) pendingAudio.removeAt(0)
            return
        }
        sendAudioNow(base64Audio)
    }

    private fun sendAudioNow(base64Audio: String) {
        val inputPayload = JSONObject().apply {
            put("realtimeInput", JSONObject().apply {
                put("audio", JSONObject().apply {
                    put("mimeType", "audio/pcm;rate=16000")
                    put("data", base64Audio)
                })
            })
        }
        webSocket?.send(inputPayload.toString())
    }

    fun disconnect() {
        pendingAudio.clear()
        webSocket?.close(1000, "User requested stop")
        webSocket = null
        onStatusChanged?.invoke("Disconnected")
    }
}
