package br.com.wgc.core.formatters.transformations

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

private const val PHONE_MAX_DIGITS = 11
private const val PHONE_MOBILE_DIGITS = 11
private const val DDD_DIGITS = 2

/**
 * [VisualTransformation] do Jetpack Compose que formata dinamicamente um campo de texto telefônico brasileiro:
 * `(XX) XXXXX-XXXX` para celulares (11 dígitos) ou `(XX) XXXX-XXXX` para telefones fixos (10 dígitos).
 *
 * Inclui cálculo adaptativo de [OffsetMapping] para manter o cursor sincronizado durante a digitação.
 */
class PhoneVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length > PHONE_MAX_DIGITS) {
            text.text.substring(0, PHONE_MAX_DIGITS)
        } else {
            text.text
        }

        val isMobile = trimmed.length == PHONE_MOBILE_DIGITS
        val out = StringBuilder()

        for (i in trimmed.indices) {
            if (i == 0) out.append('(')
            out.append(trimmed[i])
            if (i == 1) out.append(") ")
            val dashCondition = if (isMobile) i == 6 else i == 5
            if (dashCondition && i < trimmed.length - 1) {
                out.append('-')
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clampedOffset = offset.coerceAtMost(trimmed.length)
                val dashIndex = if (isMobile) 7 else 6
                return when {
                    clampedOffset <= 0 -> 0
                    clampedOffset <= DDD_DIGITS -> clampedOffset + 1
                    clampedOffset <= dashIndex -> clampedOffset + 3
                    else -> (clampedOffset + 4).coerceAtMost(out.length)
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clampedOffset = offset.coerceAtMost(out.length)
                val dashTransformedIndex = if (isMobile) 11 else 10
                val originalOffset = when {
                    clampedOffset <= 1 -> 0
                    clampedOffset <= 4 -> clampedOffset - 1
                    clampedOffset <= dashTransformedIndex -> clampedOffset - 3
                    else -> clampedOffset - 4
                }
                return originalOffset.coerceIn(0, trimmed.length)
            }
        }

        return TransformedText(AnnotatedString(out.toString()), offsetMapping)
    }
}
