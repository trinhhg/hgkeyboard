package app.funput.funput.keyboard.layout.letters

import app.funput.funput.keyboard.layout.keys.commaKey
import app.funput.funput.keyboard.layout.keys.periodKey
import app.funput.funput.keyboard.layout.rows.topNumberRowForLetters
import app.funput.funput.keyboard.model.KeyRole
import app.funput.funput.keyboard.model.KeyboardEditorMode
import app.funput.funput.keyboard.model.KeyboardInputMethod
import app.funput.funput.keyboard.model.KeyboardLayout

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
    ): KeyboardLayout = when (inputMethod) {
        KeyboardInputMethod.TELEX -> create("qwerty-telex", inputMethod, showsNumberRow)
        KeyboardInputMethod.TELEX_ADVANCED ->
            create("qwerty-telex-advanced", inputMethod, showsNumberRow)
        KeyboardInputMethod.VNI -> create("qwerty-vni", inputMethod, showsNumberRow = true)
    }

    val telex = forInputMethod(KeyboardInputMethod.TELEX, showsNumberRow = true)
    val telexAdvanced = forInputMethod(KeyboardInputMethod.TELEX_ADVANCED, showsNumberRow = true)
    val vni = forInputMethod(KeyboardInputMethod.VNI, showsNumberRow = true)

    private fun create(
        id: String,
        inputMethod: KeyboardInputMethod,
        showsNumberRow: Boolean,
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
            actionKeys = standardActionKeys(),
            supportsVietnameseAlternates = true,
            showsTelexHints = inputMethod.isTelexFamily,
        )
        // Decorate layout with SwiftKey / Laban secondary symbols on letter keys
        return CompactDigitAlternates.decorate(layout, decorateDigitsRow = isCompact)
    }

    private fun standardActionKeys() = listOf(
        specialKey("symbols", "123", KeyRole.SYMBOLS, 1.3f, "Ký hiệu"),
        specialKey("emoji", "", KeyRole.EMOJI, 1.1f, "Biểu tượng cảm xúc"),
        commaKey("comma"),
        standardSpaceKey(widthWeight = 4.2f),
        periodKey("period"),
        specialKey("enter", "", KeyRole.ENTER, 1.4f, "Enter"),
    )
}
