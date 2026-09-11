package br.com.wgc.core.camera

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Analisador de frames de câmera baseado no Google ML Kit Barcode Scanning para leitura de QR Codes.
 *
 * Implementa [ImageAnalysis.Analyzer] para processar frames em tempo real de forma assíncrona,
 * emitindo os códigos decodificados através de um [SharedFlow] seguro e fechando os buffers
 * de imagem ([ImageProxy.close]) imediatamente após a inferência para evitar gargalos no pipeline de câmera.
 *
 * ### Exemplo de Uso:
 * ```kotlin
 * val analyzer = QrCodeScannerAnalyzer()
 * val imageAnalysis = ImageAnalysis.Builder()
 *     .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
 *     .build()
 *     .also { it.setAnalyzer(cameraExecutor, analyzer) }
 *
 * // No ViewModel ou Composable:
 * lifecycleScope.launch {
 *     analyzer.scannedCodes.collect { qrContent ->
 *         println("QR Code lido: $qrContent")
 *     }
 * }
 * ```
 */
class QrCodeScannerAnalyzer(
    private val scanner: BarcodeScanner = BarcodeScanning.getClient()
) : ImageAnalysis.Analyzer {

    private val _scannedCodes = MutableSharedFlow<String>(extraBufferCapacity = 1)

    /**
     * Fluxo compartilhado ([SharedFlow]) que emite o conteúdo de texto bruto de cada QR Code identificado com sucesso.
     */
    val scannedCodes: SharedFlow<String> = _scannedCodes.asSharedFlow()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        if (barcode.format == Barcode.FORMAT_QR_CODE || barcode.valueType == Barcode.TYPE_TEXT) {
                            barcode.rawValue?.let { code ->
                                _scannedCodes.tryEmit(code)
                            }
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
