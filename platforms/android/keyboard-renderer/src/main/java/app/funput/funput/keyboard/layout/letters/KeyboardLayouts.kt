package app.funput.funput.keyboard.layout.letters

import app.funput.funput.keyboard.layout.keys.commaKey
import app.funput.funput.keyboard.layout.keys.periodKey
import app.funput.funput.keyboard.layout.rows.topNumberRowForLetters
import app.funput.funput.keyboard.model.KeyRole
import app.funput.funput.keyboard.model.KeyboardEditorMode
import app.funput.funput.keyboard.model.KeyboardInputMethod
import app.funput.funput.keyboard.model.KeyboardLayout
import app.funput.funput.keyboard.model.KeyboardStyleOptions

object KeyboardLayouts {
    /**
     * Builds the letters layout for [inputMethod].
     *
     * Factory default keeps the number row so existing geometry tests stay stable; the IME
     * always passes the persisted preference (default off for Telex-family).
     */
    fun forInputMethod(
        inputMethod: KeyboardInputMethod,
        showsNumberRow: Boolean = true,
        styleOptions: KeyboardStyleOptions = KeyboardStyleOptions.Default,
    ): KeyboardLayout = when (inputMethod) {
        KeyboardInputMethod.TELEX -> create("qwerty-telex", inputMethod, showsNumberRow, styleOptions)
        KeyboardInputMethod.TELEX_ADVANCED ->
            create("qwerty-telex-advanced", inputMethod, showsNumberRow, styleOptions)
        KeyboardInputMethod.VNI -> create("qwerty-vni", inputMethod, showsNumberRow = true, styleOptions = styleOptions)
    }

    val telex = forInputMethod(KeyboardInputMethod.TELEX, showsNumberRow = true)
    val telexAdvanced = forInputMethod(KeyboardInputMethod.TELEX_ADVANCED, showsNumberRow = true)
    val vni = forInputMethod(KeyboardInputMethod.VNI, showsNumberRow = true)

    private fun create(
        id: String,
        inputMethod: KeyboardInputMethod,
        showsNumberRow: Boolean,
        styleOptions: KeyboardStyleOptions,
    ): KeyboardLayout {
        val isCompact = usesCompactLetterRows(inputMethod, KeyboardEditorMode.TEXT, showsNumberRow)
        val layout = qwertyLayout(
            id = if (isCompact) "$id-compact" else id,
            inputMethod = inputMethod,
            leadingRows = if (isCompact) {
                emptyList()
            } else {
                listOf(topNumberRowForLetters(inputMethod))
            },
            actionKeys = if (styleOptions.showBottomEmojiKey) swiftKeyActionKeys() else labanActionKeys(),
            supportsVietnameseAlternates = true,
            showsTelexHints = inputMethod.isTelexFamily,
        )
        return CompactDigitAlternates.decorate(
            layout = layout,
            decorateDigitsRow = isCompact,
            decorateSymbolRows = styleOptions.showSecondarySymbols,
        )
    }

    /** 6-key SwiftKey bottom row: 123 | Emoji | , | < Hg > | . | Enter */
    private fun swiftKeyActionKeys() = listOf(
        specialKey("symbols", "123", KeyRole.SYMBOLS, 1.3f, "Ký hiệu"),
        specialKey("action-emoji", "", KeyRole.EMOJI, 1.1f, "Biểu tượng cảm xúc"),
        commaKey("comma"),
        standardSpaceKey(widthWeight = 4.2f),
        periodKey("period"),
        specialKey("enter", "", KeyRole.ENTER, 1.4f, "Enter"),
    )

    /** 5-key Laban Key bottom row: ?123 | , | Hg | . | Enter */
    private fun labanActionKeys() = listOf(
        specialKey("symbols", "?123", KeyRole.SYMBOLS, 1.7f, "Ký hiệu"),
        commaKey("comma"),
        standardSpaceKey(widthWeight = 5.8f),
        periodKey("period"),
        specialKey("enter", "", KeyRole.ENTER, 1.7f, "Enter"),
    )
}
