package br.com.wgc.core.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * Metadados de desempenho do frame processado pela câmera.
 */
data class FrameMetadata(
    val width: Int,
    val height: Int,
    val format: Int,
    val rotationDegrees: Int,
    val frameIndex: Long,
    val processingTimeMs: Long,
)

/**
 * Pipeline de alto desempenho para análise de imagens em tempo real via CameraX.
 * Implementa estratégia de descarte inteligente de frames quando o consumidor downstream estiver ocupado,
 * garantindo taxa de atualização suave de até 60 FPS sem retenção de memória de frames da GPU.
 */
class FrameAnalysisPipeline(
    private val frameListener: (image: ImageProxy, metadata: FrameMetadata) -> Unit,
) : ImageAnalysis.Analyzer {
    private val isBusy = AtomicBoolean(false)
    private val frameCounter = AtomicLong(0L)

    override fun analyze(image: ImageProxy) {
        // Se a etapa anterior de ML/processamento ainda estiver em execução, descarta o frame imediatamente
        if (!isBusy.compareAndSet(false, true)) {
            image.close()
            return
        }

        val startTime = System.currentTimeMillis()
        val index = frameCounter.incrementAndGet()

        try {
            val metadata =
                FrameMetadata(
                    width = image.width,
                    height = image.height,
                    format = image.format,
                    rotationDegrees = image.imageInfo.rotationDegrees,
                    frameIndex = index,
                    processingTimeMs = 0L,
                )

            frameListener(image, metadata.copy(processingTimeMs = System.currentTimeMillis() - startTime))
        } catch (ignored: Exception) {
            // Ignora exceções isoladas do analisador para não travar a câmera
        } finally {
            image.close()
            isBusy.set(false)
        }
    }

    fun getTotalFramesProcessed(): Long = frameCounter.get()
}
