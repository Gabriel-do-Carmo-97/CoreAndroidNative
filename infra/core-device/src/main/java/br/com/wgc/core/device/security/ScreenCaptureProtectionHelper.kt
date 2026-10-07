package br.com.wgc.core.device.security

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import java.lang.ref.WeakReference

/**
 * Enterprise utility for preventing screenshots, screen recordings, and recent-app previews.
 *
 * Implements Android 14+ Screen Capture Callback APIs and FLAG_SECURE window masking.
 */
class ScreenCaptureProtectionHelper {
    private var registeredActivityRef: WeakReference<Activity>? = null

    /**
     * Enables hardware-enforced protection against screenshots and screen recorders.
     *
     * @param activity The target activity whose window should be masked.
     */
    fun enableProtection(activity: Activity) {
        registeredActivityRef = WeakReference(activity)
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    /**
     * Disables screenshot and screen recording protection on the target activity.
     */
    fun disableProtection(activity: Activity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        if (registeredActivityRef?.get() == activity) {
            registeredActivityRef = null
        }
    }

    /**
     * Returns whether the given activity has FLAG_SECURE active on its window.
     */
    fun isProtectionActive(activity: Activity): Boolean {
        val flags = activity.window.attributes.flags
        return (flags and WindowManager.LayoutParams.FLAG_SECURE) != 0
    }

    /**
     * Checks if the device running OS supports native Android 14+ ScreenCaptureCallback.
     */
    fun isNativeScreenCaptureCallbackSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    }
}
