package br.com.wgc.core.formatters.transformations

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

private const val CEP_DASH_INDEX = 4
private const val CEP_DASH_ORIGINAL = 5
private const val CEP_MAX_ORIGINAL_LENGTH = 8
private const val CEP_MAX_TRANSFORMED_LENGTH = 9

/**
 * [VisualTransformation] do Jetpack Compose que mascara dinamicamente um campo de texto no padrão CEP (`00000-000`).
 *
 * Mapeia bidirecionalmente a posição do cursor ([OffsetMapping]) garantindo navegação
 * correta e prevenindo erros de índice fora do limite.
 */
class CepVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed =
            if (text.text.length > CEP_MAX_ORIGINAL_LENGTH) {
                text.text.substring(0, CEP_MAX_ORIGINAL_LENGTH)
            } else {
                text.text
            }

        val out = StringBuilder()
        for (i in trimmed.indices) {
            out.append(trimmed[i])
            if (i == CEP_DASH_INDEX) {
                out.append('-')
            }
        }

        val offsetMapping =
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    val clampedOffset = offset.coerceAtMost(trimmed.length)
                    return when {
                        clampedOffset <= CEP_DASH_ORIGINAL -> clampedOffset
                        clampedOffset <= CEP_MAX_ORIGINAL_LENGTH -> clampedOffset + 1
                        else -> CEP_MAX_TRANSFORMED_LENGTH
                    }
                }

                override fun transformedToOriginal(offset: Int): Int {
                    val clampedOffset = offset.coerceAtMost(out.length)
                    val originalOffset =
                        when {
                            clampedOffset <= CEP_DASH_ORIGINAL -> clampedOffset
                            else -> clampedOffset - 1
                        }
                    return originalOffset.coerceIn(0, trimmed.length)
                }
            }

        return TransformedText(AnnotatedString(out.toString()), offsetMapping)
    }
}
