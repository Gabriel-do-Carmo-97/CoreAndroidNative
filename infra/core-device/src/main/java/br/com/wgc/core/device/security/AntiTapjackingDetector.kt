package br.com.wgc.core.device.security

import android.view.MotionEvent
import android.view.View

/**
 * Enterprise detector and guard against tapjacking and overlay attacks (FLAG_WINDOW_IS_OBSCURED).
 */
object AntiTapjackingDetector {
    /**
     * Inspects a touch event to determine if the touch was obscured or partly obscured by an overlay.
     *
     * @param event The received [MotionEvent] from the view touch listener.
     * @return `true` if an overlay or hidden floating window is intercepting or obscuring touch coordinates.
     */
    fun isEventObscured(event: MotionEvent): Boolean {
        val flags = event.flags
        val isObscured = (flags and MotionEvent.FLAG_WINDOW_IS_OBSCURED) != 0
        val isPartlyObscured = (flags and MotionEvent.FLAG_WINDOW_IS_PARTIALLY_OBSCURED) != 0
        return isObscured || isPartlyObscured
    }

    /**
     * Applies filterTouchesWhenObscured to the given view to drop obscured touch events automatically.
     */
    fun protectView(view: View) {
        view.filterTouchesWhenObscured = true
    }
}
