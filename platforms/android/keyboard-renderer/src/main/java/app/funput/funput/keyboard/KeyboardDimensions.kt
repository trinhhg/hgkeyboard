package app.funput.funput.keyboard

import app.funput.funput.keyboard.layout.KeyboardSizingProfile
import app.funput.funput.keyboard.layout.letters.usesCompactLetterRows
import app.funput.funput.keyboard.layout.toolbar.ToolbarMetrics
import app.funput.funput.keyboard.model.KeyboardEditorMode
import app.funput.funput.keyboard.model.KeyboardInputMethod

object KeyboardDimensions {
    const val DefaultWidthDp = 360f

    private const val CanonicalColumnCount = 10

    fun recommendedHeightDp(
        inputMethod: KeyboardInputMethod,
        editorMode: KeyboardEditorMode = KeyboardEditorMode.TEXT,
        profile: KeyboardSizingProfile = KeyboardSizingProfile.Default,
        widthDp: Float = DefaultWidthDp,
        showsNumberRow: Boolean = true,
        isDualBar: Boolean = false,
    ): Float = baseRecommendedHeightDp(editorMode, profile, widthDp, inputMethod, showsNumberRow, isDualBar)

    internal fun baseRecommendedHeightDp(
        editorMode: KeyboardEditorMode,
        profile: KeyboardSizingProfile = KeyboardSizingProfile.Default,
        widthDp: Float = DefaultWidthDp,
        inputMethod: KeyboardInputMethod = KeyboardInputMethod.TELEX,
        showsNumberRow: Boolean = true,
        isDualBar: Boolean = false,
    ): Float = when {
        editorMode == KeyboardEditorMode.PIN -> heightForRowCount(4, false, profile, widthDp, isDualBar = false)
        editorMode.usesKeypad -> heightForRowCount(4, true, profile, widthDp, isDualBar = isDualBar)
        editorMode.isPassword -> heightForRowCount(5, false, profile, widthDp, isDualBar = false)
        else -> {
            val compact = usesCompactLetterRows(inputMethod, editorMode, showsNumberRow)
            heightForRowCount(if (compact) 4 else 5, true, profile, widthDp, isDualBar = isDualBar)
        }
    }

    internal fun heightForRowCount(
        rowCount: Int,
        hasSuggestionBar: Boolean,
        profile: KeyboardSizingProfile = KeyboardSizingProfile.Default,
        widthDp: Float = DefaultWidthDp,
        isDualBar: Boolean = false,
    ): Float {
        require(rowCount > 0) { "Row count must be positive" }
        val contentWidth = minOf(widthDp, DefaultWidthDp) - profile.horizontalPaddingDp * 2f
        require(contentWidth > 0f) { "Keyboard width must exceed its horizontal padding" }
        val canonicalUnit = contentWidth /
            (CanonicalColumnCount + (CanonicalColumnCount - 1) * profile.horizontalGapRatio)
        val rowHeight = canonicalUnit / profile.keyAspectRatio
        val verticalGap = rowHeight * profile.verticalGapRatio
        val rowsBlockHeight = rowCount * rowHeight + (rowCount - 1) * verticalGap
        val suggestionBarBlock = if (hasSuggestionBar) {
            if (isDualBar) ToolbarMetrics.DualChromeDp else ToolbarMetrics.ChromeDp
        } else {
            0f
        }
        return (profile.verticalPaddingDp * 2f + suggestionBarBlock + rowsBlockHeight + profile.bottomMarginDp) * profile.heightScale
    }
}
