package br.com.wgc.core.camera

import com.google.mlkit.vision.barcode.BarcodeScanner
import io.mockk.mockk
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class QrCodeScannerAnalyzerTest {

    @Test
    fun scannedCodes_initiallyHasNoEmissions() = runTest {
        val mockScanner = mockk<BarcodeScanner>(relaxed = true)
        val analyzer = QrCodeScannerAnalyzer(scanner = mockScanner)
        assertNotNull(analyzer.scannedCodes)
        assertNull(analyzer.scannedCodes.replayCache.firstOrNull())
    }
}
