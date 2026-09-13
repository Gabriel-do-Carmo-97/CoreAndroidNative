package br.com.wgc.core.ui.previews

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Multi-preview annotation providing simultaneous Light and Dark theme previews.
 */
@Preview(
    name = "Light Mode",
    group = "Themes",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
)
@Preview(
    name = "Dark Mode",
    group = "Themes",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
annotation class ThemePreviews

/**
 * Multi-preview annotation providing previews across phone, tablet, and foldable form factors.
 */
@Preview(
    name = "Phone",
    group = "Devices",
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true,
)
@Preview(
    name = "Tablet",
    group = "Devices",
    device = "spec:width=1280dp,height=800dp,dpi=240",
    showSystemUi = true,
)
@Preview(
    name = "Foldable",
    group = "Devices",
    device = "spec:width=673dp,height=841dp",
    showSystemUi = true,
)
annotation class DevicePreviews

/**
 * Multi-preview annotation providing accessibility verification for regular and enlarged typography.
 */
@Preview(
    name = "Font Normal (1.0x)",
    group = "Font Scale",
    fontScale = 1.0f,
    showBackground = true,
)
@Preview(
    name = "Font Large (1.5x)",
    group = "Font Scale",
    fontScale = 1.5f,
    showBackground = true,
)
annotation class FontScalePreviews

/**
 * Enterprise combined multi-preview covering standard themes and device form factors.
 */
@ThemePreviews
@DevicePreviews
annotation class CompletePreviews
