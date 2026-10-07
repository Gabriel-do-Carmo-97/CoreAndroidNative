package br.com.wgc.core.camera.hdr

import android.os.Build

/**
 * Output dynamic range capabilities supported by CameraX.
 */
enum class DynamicRangeCapability {
    SDR,
    HLG_10_BIT,
    HDR10,
    HDR10_PLUS,
    DOLBY_VISION_10_BIT,
}

/**
 * Enterprise inspector for Android 14+ Ultra HDR image capture and 10-bit HDR video recording.
 */
object UltraHdrInspector {
    /**
     * Checks if Ultra HDR (JPEG_R / Gainmap) image capture is natively supported by the OS.
     */
    fun isUltraHdrCaptureSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    }

    /**
     * Returns true if 10-bit dynamic range video encoding is available on host Android build.
     */
    fun isTenBitHdrSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }
}
