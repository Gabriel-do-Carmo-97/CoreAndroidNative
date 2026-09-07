package br.com.wgc.core.device

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

private const val CLICK_DURATION_MS = 20L
private const val CLICK_AMPLITUDE = 60

private const val SUCCESS_DURATION_1_MS = 30L
private const val SUCCESS_PAUSE_MS = 60L
private const val SUCCESS_DURATION_2_MS = 40L

private const val ERROR_DURATION_1_MS = 50L
private const val ERROR_PAUSE_MS = 40L
private const val ERROR_DURATION_2_MS = 50L

/**
 * Contrato de utilitário para disparo de feedbacks hápticos (vibração tátil) na interface do usuário.
 */
interface HapticFeedbackHelper {

    /** Dispara uma vibração sutil simulando o clique ou toque em botão. */
    fun vibrateClick()

    /** Dispara um padrão tátil duplo indicativo de sucesso em operações assíncronas. */
    fun vibrateSuccess()

    /** Dispara um padrão tátil firme indicativo de falha, erro de validação ou ação bloqueada. */
    fun vibrateError()
}

/**
 * Implementação padrão de [HapticFeedbackHelper] com compatibilidade entre versões de API do Android.
 *
 * Utiliza [VibratorManager] no Android 12+ (API 31+) e realiza fallback seguro para [Vibrator] em versões anteriores,
 * tratando silenciosamente ausência de permissão ou falta de atuador físico no dispositivo.
 *
 * @property context Contexto da aplicação utilizado para acessar o serviço de vibração.
 */
class DefaultHapticFeedbackHelper(private val context: Context) : HapticFeedbackHelper {

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (@Suppress("SwallowedException", "TooGenericExceptionCaught") e: Exception) {
            null
        }
    }

    override fun vibrateClick() {
        performVibration(
            effect = { VibrationEffect.createOneShot(CLICK_DURATION_MS, CLICK_AMPLITUDE) },
            fallbackDuration = CLICK_DURATION_MS
        )
    }

    override fun vibrateSuccess() {
        val timings = longArrayOf(0, SUCCESS_DURATION_1_MS, SUCCESS_PAUSE_MS, SUCCESS_DURATION_2_MS)
        val amplitudes = intArrayOf(0, 100, 0, 180)
        performVibration(
            effect = { VibrationEffect.createWaveform(timings, amplitudes, -1) },
            fallbackDuration = SUCCESS_DURATION_1_MS + SUCCESS_DURATION_2_MS
        )
    }

    override fun vibrateError() {
        val timings = longArrayOf(0, ERROR_DURATION_1_MS, ERROR_PAUSE_MS, ERROR_DURATION_2_MS)
        val amplitudes = intArrayOf(0, 255, 0, 255)
        performVibration(
            effect = { VibrationEffect.createWaveform(timings, amplitudes, -1) },
            fallbackDuration = ERROR_DURATION_1_MS + ERROR_DURATION_2_MS
        )
    }

    @Suppress("SwallowedException", "TooGenericExceptionCaught")
    private fun performVibration(
        effect: () -> VibrationEffect,
        fallbackDuration: Long
    ) {
        val activeVibrator = vibrator ?: return
        if (!activeVibrator.hasVibrator()) return

        try {
            activeVibrator.vibrate(effect())
        } catch (e: Exception) {
            // Falha silenciosa para evitar crashes em emuladores ou sem permissão declarada
            try {
                @Suppress("DEPRECATION")
                activeVibrator.vibrate(fallbackDuration)
            } catch (ignored: Exception) {
                // Ignore fallback failure
            }
        }
    }
}
