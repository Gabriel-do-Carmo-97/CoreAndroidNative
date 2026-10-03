package br.com.wgc.core.ai.gemini

import kotlinx.coroutines.flow.Flow

/**
 * Prompt execution request sent to the on-device Edge AI model.
 *
 * @property prompt Input prompt text.
 * @property temperature Sampling temperature (0.0 for deterministic output).
 * @property topK Top-k sampling limit.
 */
data class GeminiNanoPrompt(
    val prompt: String,
    val temperature: Float = DEFAULT_TEMPERATURE,
    val topK: Int = DEFAULT_TOP_K,
) {
    companion object {
        const val DEFAULT_TEMPERATURE = 0.2f
        const val DEFAULT_TOP_K = 16
    }
}

/**
 * Interface contract for on-device Gemini Nano inference using Android AICore.
 */
interface GeminiNanoClient {
    /**
     * Checks if on-device Gemini Nano model is available and downloaded on this device.
     */
    suspend fun isModelAvailable(): Boolean

    /**
     * Generates a single text response synchronously for the given prompt.
     */
    suspend fun generateResponse(request: GeminiNanoPrompt): String

    /**
     * Streams responses token-by-token as they are generated on the NPU/GPU.
     */
    fun streamResponse(request: GeminiNanoPrompt): Flow<String>
}
