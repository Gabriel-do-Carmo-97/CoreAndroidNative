package br.com.wgc.core.extensions

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role

/**
 * Extensão de Modifier para prevenir múltiplos cliques acidentais rápidos (debounce).
 *
 * @param debounceTimeMs Intervalo mínimo entre cliques válidos em milissegundos. Padrão: 600ms.
 * @param enabled Indica se o clique está habilitado. Padrão: true.
 * @param showRipple Se verdadeiro, exibe a indicação visual de clique (ripple). Se falso, o clique é invisível.
 * @param onClickLabel Descrição de acessibilidade para a ação de clique.
 * @param role Tipo de elemento semântico para acessibilidade (ex: Button, Checkbox).
 * @param onClick Ação executada quando o clique respeita o intervalo de debounce.
 */
fun Modifier.debouncedClick(
    debounceTimeMs: Long = 600L,
    enabled: Boolean = true,
    showRipple: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier =
    composed {
        var lastClickTime by remember { mutableLongStateOf(0L) }

        val debouncedOnClick = {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= debounceTimeMs) {
                lastClickTime = currentTime
                onClick()
            }
        }

        if (showRipple) {
            this.clickable(
                enabled = enabled,
                onClickLabel = onClickLabel,
                role = role,
                onClick = debouncedOnClick,
            )
        } else {
            this.clickable(
                enabled = enabled,
                onClickLabel = onClickLabel,
                role = role,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = debouncedOnClick,
            )
        }
    }

/**
 * Extensão de Modifier para cliques com debounce com customização explícita
 * de [MutableInteractionSource] e [Indication].
 */
fun Modifier.debouncedClick(
    interactionSource: MutableInteractionSource,
    indication: Indication?,
    debounceTimeMs: Long = 600L,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier =
    composed {
        var lastClickTime by remember { mutableLongStateOf(0L) }

        this.clickable(
            interactionSource = interactionSource,
            indication = indication,
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role,
        ) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= debounceTimeMs) {
                lastClickTime = currentTime
                onClick()
            }
        }
    }
