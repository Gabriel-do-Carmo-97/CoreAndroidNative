package br.com.wgc.core.formatters.transformations

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

private const val CPF_FIRST_DOT_INDEX = 2
private const val CPF_SECOND_DOT_INDEX = 5
private const val CPF_DASH_INDEX = 8

private const val CPF_FIRST_DOT_ORIGINAL = 3
private const val CPF_SECOND_DOT_ORIGINAL = 6
private const val CPF_DASH_ORIGINAL = 9
private const val CPF_MAX_ORIGINAL_LENGTH = 11

private const val CPF_FIRST_DOT_TRANSFORMED = 4
private const val CPF_SECOND_DOT_TRANSFORMED = 8
private const val CPF_DASH_TRANSFORMED = 12
private const val CPF_MAX_TRANSFORMED_LENGTH = 14

/**
 * [VisualTransformation] do Jetpack Compose que mascara dinamicamente um campo de texto no padrão CPF (`000.000.000-00`).
 *
 * Mapeia bidirecionalmente a posição do cursor ([OffsetMapping]) para manter a navegação e edição
 * naturais do usuário sem alterar o valor bruto retido no estado subjacente.
 */
class CpfVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed =
            if (text.text.length > CPF_MAX_ORIGINAL_LENGTH) {
                text.text.substring(0, CPF_MAX_ORIGINAL_LENGTH)
            } else {
                text.text
            }

        val out = StringBuilder()
        for (i in trimmed.indices) {
            out.append(trimmed[i])
            if (i == CPF_FIRST_DOT_INDEX || i == CPF_SECOND_DOT_INDEX) {
                out.append('.')
            } else if (i == CPF_DASH_INDEX) {
                out.append('-')
            }
        }

        val offsetMapping =
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    val clampedOffset = offset.coerceAtMost(trimmed.length)
                    return when {
                        clampedOffset <= CPF_FIRST_DOT_ORIGINAL -> clampedOffset
                        clampedOffset <= CPF_SECOND_DOT_ORIGINAL -> clampedOffset + 1
                        clampedOffset <= CPF_DASH_ORIGINAL -> clampedOffset + 2
                        clampedOffset <= CPF_MAX_ORIGINAL_LENGTH -> clampedOffset + 3
                        else -> CPF_MAX_TRANSFORMED_LENGTH
                    }
                }

                override fun transformedToOriginal(offset: Int): Int {
                    val clampedOffset = offset.coerceAtMost(out.length)
                    val originalOffset =
                        when {
                            clampedOffset <= CPF_FIRST_DOT_TRANSFORMED - 1 -> clampedOffset
                            clampedOffset <= CPF_SECOND_DOT_TRANSFORMED - 1 -> clampedOffset - 1
                            clampedOffset <= CPF_DASH_TRANSFORMED - 1 -> clampedOffset - 2
                            else -> clampedOffset - 3
                        }
                    return originalOffset.coerceIn(0, trimmed.length)
                }
            }

        return TransformedText(AnnotatedString(out.toString()), offsetMapping)
    }
}
