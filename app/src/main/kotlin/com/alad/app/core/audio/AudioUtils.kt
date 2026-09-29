package com.alad.app.core.audio

import kotlin.math.sqrt

/**
 * Utility functions for audio signal processing and amplitude calculation.
 */
object AudioUtils {

    private const val MAX_AMPLITUDE_16BIT = 32767f

    /**
     * Calculates the Root Mean Square (RMS) of 16-bit signed PCM audio bytes.
     */
    fun calculateRms(pcmData: ByteArray): Float {
        if (pcmData.isEmpty()) return 0f

        var sum = 0.0
        val sampleCount = pcmData.size / 2

        for (i in 0 until sampleCount) {
            val byteIndex = i * 2
            if (byteIndex + 1 < pcmData.size) {
                val low = pcmData[byteIndex].toInt() and 0xFF
                val high = pcmData[byteIndex + 1].toInt() shl 8
                val sample = (low or high).toShort().toFloat()
                sum += sample * sample
            }
        }

        return if (sampleCount > 0) {
            sqrt(sum / sampleCount).toFloat()
        } else {
            0f
        }
    }

    /**
     * Normalizes the RMS value to a 0.0f - 1.0f range with an optional visual boost factor.
     */
    fun normalizeRms(rms: Float, boostFactor: Float = 3.0f): Float {
        return (rms / MAX_AMPLITUDE_16BIT * boostFactor).coerceIn(0f, 1f)
    }

    /**
     * Applies a low-pass filter to smooth amplitude transitions for fluid UI animations.
     */
    fun smoothAmplitude(current: Float, target: Float, alpha: Float = 0.5f): Float {
        return current * (1f - alpha) + target * alpha
    }

    /**
     * Complete pipeline: takes raw PCM data and the previous amplitude,
     * calculates RMS, normalizes it, and returns the smoothed amplitude.
     */
    fun processPcmAmplitude(pcmData: ByteArray, currentAmplitude: Float): Float {
        val rms = calculateRms(pcmData)
        val normalized = normalizeRms(rms, boostFactor = 3.0f)
        return smoothAmplitude(current = currentAmplitude, target = normalized, alpha = 0.5f)
    }
}
