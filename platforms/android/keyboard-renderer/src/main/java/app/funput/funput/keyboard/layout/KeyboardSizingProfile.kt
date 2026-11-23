package app.funput.funput.keyboard.layout

/** Density-independent keyboard geometry, scaled by a single user-chosen factor. */
data class KeyboardSizingProfile(
    val horizontalPaddingDp: Float = 4f,
    val verticalPaddingDp: Float = 4f,
    val horizontalGapRatio: Float = 0.095f,
    val verticalGapRatio: Float = 0.13f,
    val keyAspectRatio: Float = DefaultKeyAspectRatio,
    val heightScale: Float = 1f,
    val labelScale: Float = BaseLabelScale,
    val bottomMarginDp: Float = 0f,
) {
    init {
        require(horizontalPaddingDp >= 0f) { "Horizontal padding must not be negative" }
        require(verticalPaddingDp >= 0f) { "Vertical padding must not be negative" }
        require(horizontalGapRatio >= 0f) { "Horizontal gap ratio must not be negative" }
        require(verticalGapRatio >= 0f) { "Vertical gap ratio must not be negative" }
        require(keyAspectRatio > 0f) { "Key aspect ratio must be positive" }
        require(heightScale > 0f) { "Height scale must be positive" }
        require(labelScale > 0f) { "Label scale must be positive" }
        require(bottomMarginDp >= 0f) { "Bottom margin must not be negative" }
    }

    companion object {
        /**
         * Calibrated for 20:9 displays (Vivo iQOO Neo 10) matching Laban Key (`~257dp` 5-row,
         * `~206dp` 4-row) and SwiftKey proportions so VNI digits sit comfortably in thumb reach.
         */
        const val DefaultKeyAspectRatio = 0.80f

        /** The range Settings offers. Portrait honors the full span; landscape phones stay capped. */
        const val MinScale = 0.82f
        const val MaxScale = 1.32f
        const val DefaultScale = 1.0f

        const val PhoneLandscapeMaxScale = 0.90f

        val Normal = scaled(1f)

        val Default: KeyboardSizingProfile = scaled(DefaultScale)

        fun scaled(
            scale: Float,
            labelScale: Float = BaseLabelScale + (scale - 1f),
            bottomMarginDp: Float = 0f,
        ): KeyboardSizingProfile {
            val clamped = scale.coerceIn(MinScale, MaxScale)
            return KeyboardSizingProfile(
                heightScale = clamped,
                labelScale = labelScale.coerceIn(0.75f, 1.35f),
                bottomMarginDp = bottomMarginDp.coerceIn(0f, 32f),
            )
        }

        private const val BaseLabelScale = 0.94f
    }

    fun constrainedForLandscape(isPhoneLandscape: Boolean): KeyboardSizingProfile =
        if (isPhoneLandscape && heightScale > PhoneLandscapeMaxScale) {
            scaled(PhoneLandscapeMaxScale, labelScale = labelScale, bottomMarginDp = bottomMarginDp)
        } else {
            this
        }
}
