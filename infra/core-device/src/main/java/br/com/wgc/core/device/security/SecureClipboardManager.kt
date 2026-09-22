package br.com.wgc.core.device.security

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.PersistableBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Gerenciador corporativo para cópia segura de dados confidenciais na área de transferência (Clipboard).
 *
 * Provê proteção contra visualização de prévias em tela no Android 13+ (`EXTRA_IS_SENSITIVE`)
 * e destruição automática (auto-clear) após intervalo configurável.
 *
 * @property context Contexto de aplicação para obter o [ClipboardManager].
 * @property coroutineScope Escopo para agendamento da auto-limpeza em segundo plano.
 */
class SecureClipboardManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main),
) {
    private val clipboardManager: ClipboardManager? by lazy {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    }

    /**
     * Copia texto sensível (como chaves Pix, tokens ou senhas) marcando-o como confidencial
     * e agendando a limpeza automática do clipboard após [autoClearDelayMs].
     */
    fun copySensitive(
        label: String,
        text: String,
        autoClearDelayMs: Long = DEFAULT_AUTO_CLEAR_MS,
    ) {
        val clip =
            ClipData.newPlainText(label, text).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    description.extras =
                        PersistableBundle().apply {
                            putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
                        }
                }
            }

        clipboardManager?.setPrimaryClip(clip)

        if (autoClearDelayMs > 0) {
            coroutineScope.launch {
                delay(autoClearDelayMs)
                // Limpa apenas se o item atual ainda for o que acabamos de colocar
                val currentClip = clipboardManager?.primaryClip
                if (currentClip != null && currentClip.itemCount > 0 && currentClip.getItemAt(0)?.text == text) {
                    clearClipboard()
                }
            }
        }
    }

    /**
     * Remove imediatamente qualquer conteúdo presente na área de transferência.
     */
    fun clearClipboard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            clipboardManager?.clearPrimaryClip()
        } else {
            clipboardManager?.setPrimaryClip(ClipData.newPlainText("", ""))
        }
    }

    /**
     * Verifica se há algum conteúdo presente na área de transferência.
     */
    fun hasClipboardData(): Boolean {
        return clipboardManager?.hasPrimaryClip() == true
    }

    companion object {
        const val DEFAULT_AUTO_CLEAR_MS = 60_000L
    }
}
