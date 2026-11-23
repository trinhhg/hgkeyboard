package app.funput.funput.keyboard.layout

import app.funput.funput.keyboard.layout.toolbar.ToolbarMetrics

data class KeyboardGeometrySpec(
    val horizontalPadding: Float,
    val verticalPadding: Float,
    val horizontalGap: Float,
    val verticalGap: Float,
    val horizontalGapRatio: Float,
    val verticalGapRatio: Float,
    val keyAspectRatio: Float,
    val suggestionBarHeight: Float,
    val suggestionBarGap: Float,
    val heightScale: Float = 1f,
    val isDualBar: Boolean = false,
    val toolbarRowHeight: Float = 0f,
    val dualRowGap: Float = 0f,
    val bottomMargin: Float = 0f,
) {
    init {
        require(horizontalPadding >= 0f) { "Horizontal padding must not be negative" }
        require(verticalPadding >= 0f) { "Vertical padding must not be negative" }
        require(horizontalGap >= 0f) { "Horizontal gap must not be negative" }
        require(verticalGap >= 0f) { "Vertical gap must not be negative" }
        require(horizontalGapRatio >= 0f) { "Horizontal gap ratio must not be negative" }
        require(verticalGapRatio >= 0f) { "Vertical gap ratio must not be negative" }
        require(keyAspectRatio > 0f) { "Key aspect ratio must be positive" }
        require(suggestionBarHeight > 0f) { "Suggestion bar height must be positive" }
        require(suggestionBarGap >= 0f) { "Suggestion bar gap must not be negative" }
        require(heightScale > 0f) { "Height scale must be positive" }
    }

    companion object {
        fun fromProfile(
            density: Float,
            profile: KeyboardSizingProfile,
            isDualBar: Boolean = false,
        ): KeyboardGeometrySpec {
            require(density > 0f) { "Density must be positive" }
            val heightScale = profile.heightScale
            val suggestionHeightDp = if (isDualBar) {
                ToolbarMetrics.DualSuggestionRowHeightDp
            } else {
                ToolbarMetrics.SuggestionBarHeightDp
            }
            return KeyboardGeometrySpec(
                horizontalPadding = profile.horizontalPaddingDp * density,
                verticalPadding = profile.verticalPaddingDp * density * heightScale,
                horizontalGap = 0f,
                verticalGap = 0f,
                horizontalGapRatio = profile.horizontalGapRatio,
                verticalGapRatio = profile.verticalGapRatio,
                keyAspectRatio = profile.keyAspectRatio,
                suggestionBarHeight = suggestionHeightDp * density * heightScale,
                suggestionBarGap = ToolbarMetrics.SuggestionBarGapDp * density * heightScale,
                heightScale = heightScale,
                isDualBar = isDualBar,
                toolbarRowHeight = (if (isDualBar) ToolbarMetrics.DualUtilityRowHeightDp else 0f) * density * heightScale,
                dualRowGap = (if (isDualBar) ToolbarMetrics.DualRowGapDp else 0f) * density * heightScale,
                bottomMargin = profile.bottomMarginDp * density * heightScale,
            )
        }

        fun fromDensity(density: Float): KeyboardGeometrySpec =
            fromProfile(density, KeyboardSizingProfile.Default)
    }
}
