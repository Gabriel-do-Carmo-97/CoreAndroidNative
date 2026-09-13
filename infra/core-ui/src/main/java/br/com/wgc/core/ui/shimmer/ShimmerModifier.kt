package br.com.wgc.core.ui.shimmer

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

private const val DEFAULT_SHIMMER_DURATION_MS = 1000
private const val SHIMMER_OFFSET_MULTIPLIER = 2f
private const val COLOR_BASE = 0xFFE0E0E0
private const val COLOR_HIGHLIGHT = 0xFFF5F5F5

/**
 * Applies an animated shimmer loading skeleton effect to the composable.
 *
 * @param showShimmer Whether the shimmer animation is active. If false, leaves background untouched.
 * @param colors Color gradient used to render the moving shimmer band.
 * @param durationMillis Duration of a single shimmer swipe animation cycle in milliseconds.
 * @return [Modifier] configured with the shimmer background.
 */
fun Modifier.shimmerEffect(
    showShimmer: Boolean = true,
    colors: List<Color> =
        listOf(
            Color(COLOR_BASE),
            Color(COLOR_HIGHLIGHT),
            Color(COLOR_BASE),
        ),
    durationMillis: Int = DEFAULT_SHIMMER_DURATION_MS,
): Modifier =
    composed {
        if (!showShimmer) return@composed this

        var size by remember { mutableStateOf(IntSize.Zero) }
        val transition = rememberInfiniteTransition(label = "ShimmerTransition")
        val startOffsetX by transition.animateFloat(
            initialValue = -SHIMMER_OFFSET_MULTIPLIER * size.width.toFloat(),
            targetValue = SHIMMER_OFFSET_MULTIPLIER * size.width.toFloat(),
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "ShimmerOffsetAnimation",
        )

        this
            .onGloballyPositioned { size = it.size }
            .background(
                brush =
                    Brush.linearGradient(
                        colors = colors,
                        start = Offset(startOffsetX, 0f),
                        end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat()),
                    ),
            )
    }
