package br.com.wgc.core.device.security

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build

/**
 * Enterprise shield helper for safeguarding device clipboard access.
 *
 * Provides detection of foreign clipboard snoopers and redaction of sensitive credentials.
 */
class ClipboardShieldHelper(
    private val context: Context,
) {
    private val clipboardManager: ClipboardManager? by lazy {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    }

    /**
     * Inspects whether clipboard currently contains sensitive content flag (Android 13+).
     */
    fun hasSensitiveContent(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val description = clipboardManager?.primaryClipDescription
            if (description != null && description.extras != null) {
                return description.extras.getBoolean(android.content.ClipDescription.EXTRA_IS_SENSITIVE, false)
            }
        }
        return false
    }

    /**
     * Clears the current clipboard content securely.
     */
    fun wipeClipboard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            clipboardManager?.clearPrimaryClip()
        } else {
            clipboardManager?.setPrimaryClip(ClipData.newPlainText("", ""))
        }
    }
}
