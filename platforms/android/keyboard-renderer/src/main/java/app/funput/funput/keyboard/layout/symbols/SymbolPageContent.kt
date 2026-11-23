package app.funput.funput.keyboard.layout.symbols

/**
 * Symbol key labels grouped by page and row matching Laban Key 5-row symbol ordering.
 *
 * When a top number row (`1..0`) is present, primary Row 1 carries Laban's shift/math symbols
 * and `@` (`primaryRow2`) sits on the **second row below numbers**, followed by `* " ' : ; ! ?`.
 * `_` lives on the bottom action row next to `.` and `123` (Numpad). No glyph repeats.
 */
internal object SymbolPageContent {
    val primaryRow1 = listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆")
    val primaryRow2 = listOf("@", "#", "₫", "%", "&", "-", "+", "(", ")", "/")
    val primaryRow3 = listOf("*", "\"", "'", ":", ";", "!", "?")

    val secondaryRow1 = listOf("$", "€", "£", "¥", "¢", "^", "°", "=", "{", "}")
    val secondaryRow2 = listOf("±", "≤", "≥", "≠", "≈", "℅", "\\", "©", "®", "™")
    val secondaryRow3 = listOf("…", "<", ">", "[", "]", "✓", "∞")
}
