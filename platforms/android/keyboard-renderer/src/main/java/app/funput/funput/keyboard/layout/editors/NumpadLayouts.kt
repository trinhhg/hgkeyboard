package app.funput.funput.keyboard.layout.editors

import app.funput.funput.keyboard.KeyboardFeatures
import app.funput.funput.keyboard.layout.keys.keypadDigit
import app.funput.funput.keyboard.layout.letters.keyboardToolbarSpec
import app.funput.funput.keyboard.layout.letters.specialKey
import app.funput.funput.keyboard.model.KeyRole
import app.funput.funput.keyboard.model.KeySpec
import app.funput.funput.keyboard.model.KeyboardInputMethod
import app.funput.funput.keyboard.model.KeyboardLayout
import app.funput.funput.keyboard.model.KeyboardRow

/**
 * Laban Key style 4x5 dedicated numeric/dialpad keypad (`1234`), switchable from `?123` or toolbar.
 *
 * Row 1: `,  1  2  3  -`
 * Row 2: `(  4  5  6  .`
 * Row 3: `)  7  8  9  ⌫`
 * Row 4: `ABC  ?123  0  [space]  Enter`
 */
internal object NumpadLayouts {
    fun resolve(
        inputMethod: KeyboardInputMethod,
        suggestionsEnabled: Boolean = KeyboardFeatures.SuggestionsEnabled,
        secure: Boolean = false,
    ): KeyboardLayout = KeyboardLayout(
        id = "numpad-${inputMethod.name.lowercase()}${if (secure) "-secure" else ""}",
        inputMethod = inputMethod,
        suggestionBar = if (KeyboardFeatures.EmojiToolbarEnabled && !secure) {
            keyboardToolbarSpec(
                idSuffix = "-numpad",
                suggestionsEnabled = KeyboardFeatures.SuggestionsEnabled && suggestionsEnabled,
            )
        } else {
            null
        },
        rows = listOf(
            KeyboardRow(
                listOf(
                    sideSymbol("comma", ",", "Dấu phẩy", SideLeftWeight),
                    digit('1'),
                    digit('2'),
                    digit('3'),
                    sideSymbol("minus", "-", "Dấu trừ", SideRightWeight),
                ),
            ),
            KeyboardRow(
                listOf(
                    sideSymbol("lparen", "(", "Mở ngoặc", SideLeftWeight),
                    digit('4'),
                    digit('5'),
                    digit('6'),
                    sideSymbol("period", ".", "Dấu chấm", SideRightWeight),
                ),
            ),
            KeyboardRow(
                listOf(
                    sideSymbol("rparen", ")", "Đóng ngoặc", SideLeftWeight),
                    digit('7'),
                    digit('8'),
                    digit('9'),
                    specialKey("numpad-backspace", "", KeyRole.BACKSPACE, SideRightWeight, "Xóa"),
                ),
            ),
            KeyboardRow(
                listOf(
                    specialKey("numpad-letters", "ABC", KeyRole.LETTERS, SideLeftWeight, "Chữ cái"),
                    specialKey("numpad-symbols", "?123", KeyRole.SYMBOLS, DigitWeight, "Ký hiệu"),
                    digit('0'),
                    specialKey("numpad-space", " ", KeyRole.SPACE, DigitWeight, "Dấu cách"),
                    specialKey("numpad-enter", "", KeyRole.ENTER, SideRightWeight, "Enter"),
                ),
            ),
        ),
    )

    private fun digit(value: Char) = keypadDigit(value).copy(widthWeight = DigitWeight)

    private fun sideSymbol(id: String, label: String, accessibility: String, weight: Float) = KeySpec(
        id = "numpad-$id",
        label = label,
        role = KeyRole.PUNCTUATION,
        widthWeight = weight,
        accessibilityLabel = accessibility,
    )

    private const val SideLeftWeight = 0.85f
    private const val DigitWeight = 1.15f
    private const val SideRightWeight = 0.95f
}
