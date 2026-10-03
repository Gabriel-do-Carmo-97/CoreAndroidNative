package br.com.wgc.core.ai.runtime

/**
 * Execution acceleration target hardware for machine learning models.
 */
enum class ModelExecutionHardware {
    CPU,
    GPU,
    NNAPI_NPU,
}

/**
 * Enterprise contract for running ONNX Runtime / TFLite models on-device.
 */
interface OnnxRuntimeBridge {
    /**
     * Executes inference on the loaded model using the input tensor mapping.
     *
     * @param inputTensors Map of input tensor name to array of float values.
     * @return Map of output tensor name to output float array.
     */
    suspend fun runInference(inputTensors: Map<String, FloatArray>): Map<String, FloatArray>

    /**
     * Returns the active hardware acceleration backend in use.
     */
    fun getActiveHardware(): ModelExecutionHardware

    /**
     * Releases model handles and tensor buffers.
     */
    fun close()
}
