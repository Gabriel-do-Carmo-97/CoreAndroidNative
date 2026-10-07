package br.com.wgc.core.camera.ocr

import android.graphics.Rect

/**
 * Extracted text block bounding region and content recognized by on-device OCR.
 *
 * @property text Recognized string text.
 * @property boundingBox Rectangular coordinate bounds of the text in image frame.
 * @property confidence Recognition confidence percentage (0.0 to 1.0).
 */
data class RecognizedTextBlock(
    val text: String,
    val boundingBox: Rect? = null,
    val confidence: Float = 1.0f,
)

/**
 * Interface contract for on-device ML Kit OCR / Document scanning.
 */
interface OnDeviceOcrAnalyzer {
    /**
     * Extracts recognized text blocks from an incoming image frame or bitmap.
     */
    suspend fun processImage(
        imageBytes: ByteArray,
        width: Int,
        height: Int,
    ): List<RecognizedTextBlock>
}
