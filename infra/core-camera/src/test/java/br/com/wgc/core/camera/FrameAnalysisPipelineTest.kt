package br.com.wgc.core.camera

import androidx.camera.core.ImageInfo
import androidx.camera.core.ImageProxy
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class FrameAnalysisPipelineTest {
    @Test
    fun `analyze processes frame and closes imageProxy`() {
        var processedMetadata: FrameMetadata? = null
        val pipeline =
            FrameAnalysisPipeline { _, metadata ->
                processedMetadata = metadata
            }

        val imageProxy: ImageProxy = mockk(relaxed = true)
        val imageInfo: ImageInfo = mockk(relaxed = true)
        every { imageProxy.width } returns 1920
        every { imageProxy.height } returns 1080
        every { imageProxy.imageInfo } returns imageInfo
        every { imageInfo.rotationDegrees } returns 90

        pipeline.analyze(imageProxy)

        verify { imageProxy.close() }
        assertEquals(1920, processedMetadata?.width)
        assertEquals(1080, processedMetadata?.height)
        assertEquals(90, processedMetadata?.rotationDegrees)
        assertEquals(1L, pipeline.getTotalFramesProcessed())
    }
}
