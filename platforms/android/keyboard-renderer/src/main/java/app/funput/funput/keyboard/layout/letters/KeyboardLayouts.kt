package app.funput.funput.keyboard.layout.letters

import app.funput.funput.keyboard.layout.keys.commaKey
import app.funput.funput.keyboard.layout.keys.periodKey
import app.funput.funput.keyboard.layout.rows.topNumberRowForLetters
import app.funput.funput.keyboard.model.KeyRole
import app.funput.funput.keyboard.model.KeySpec
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
                listOf(topNumberRowForLetters(inputMethod, styleOptions.showVniAccentsOnNumberRow))
            },
            actionKeys = buildActionKeys(styleOptions),
            supportsVietnameseAlternates = true,
            showsTelexHints = inputMethod.isTelexFamily,
        )
        return CompactDigitAlternates.decorate(
            layout = layout,
            decorateDigitsRow = isCompact && styleOptions.showSecondarySymbols,
            decorateSymbolRows = styleOptions.showSecondarySymbols,
        )
    }

    private fun buildActionKeys(styleOptions: KeyboardStyleOptions): List<KeySpec> = buildList {
        val hasLang = styleOptions.showLanguageSwitchKey
        val hasPunct = styleOptions.showCommaPeriodKeys
        add(specialKey("symbols", "?123", KeyRole.SYMBOLS, 1.55f, "Ký hiệu"))
        if (hasLang) {
            add(specialKey("action-language", "", KeyRole.SYSTEM_INPUT_METHOD, 1.0f, "Đổi ngôn ngữ"))
        }
        if (hasPunct) {
            add(commaKey("comma"))
        }
        val spaceWeight = when {
            !hasPunct && hasLang -> 5.8f
            !hasPunct -> 6.8f
            hasLang -> 4.35f
            else -> 5.8f
        }
        add(standardSpaceKey(widthWeight = spaceWeight, swipeSwitchesLanguage = styleOptions.spaceSwipeSwitchesLanguage))
        if (hasPunct) {
            add(periodKey("period"))
        }
        add(specialKey("enter", "", KeyRole.ENTER, 1.6f, "Enter"))
    }
}
