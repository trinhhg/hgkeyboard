package app.funput.funput.keyboard.layout.letters

import app.funput.funput.keyboard.model.KeyRole
import app.funput.funput.keyboard.model.KeySpec
import app.funput.funput.keyboard.model.KeyboardLayout
import app.funput.funput.keyboard.model.KeyboardRow
import app.funput.funput.keyboard.popover.model.KeyAlternate

/**
 * Secondary symbols and long-press alternates matching Microsoft SwiftKey and Laban Key:
 * - Row 1 (top): qwertyuiop -> 1 2 3 4 5 6 7 8 9 0
 * - Row 2 (middle): asdfghjkl -> @ # & * - + = ( )
 * - Row 3 (bottom): zxcvbnm -> _ đ " ' : ; /
 */
internal object CompactDigitAlternates {
    private const val Row1Characters = "qwertyuiop"
    private const val Row1Symbols = "1234567890"

    private const val Row2Characters = "asdfghjkl"
    private const val Row2Symbols = "@#&*-+=()"

    private const val Row3Characters = "zxcvbnm"
    private const val Row3Symbols = "_đ\"':;/"

    fun decorate(layout: KeyboardLayout, decorateDigitsRow: Boolean = true): KeyboardLayout =
        layout.copy(rows = layout.rows.map { decorate(it, decorateDigitsRow) })

    private fun decorate(row: KeyboardRow, decorateDigitsRow: Boolean): KeyboardRow {
        val letterLabels = row.keys
            .filter { it.role == KeyRole.CHARACTER }
            .map { it.label }
            .joinToString("")

        return when (letterLabels) {
            Row1Characters -> {
                if (decorateDigitsRow) {
                    row.copy(keys = row.keys.mapIndexed { index, key -> apply(Row1Symbols[index], key, "số") })
                } else {
                    row
                }
            }
            Row2Characters -> {
                row.copy(keys = row.keys.mapIndexed { index, key -> apply(Row2Symbols[index], key, "ký tự") })
            }
            Row3Characters -> {
                row.copy(keys = row.keys.map { key ->
                    val idx = Row3Characters.indexOf(key.label.firstOrNull() ?: ' ')
                    if (idx >= 0 && idx < Row3Symbols.length) {
                        apply(Row3Symbols[idx], key, "ký tự")
                    } else {
                        key
                    }
                })
            }
            else -> row
        }
    }

    private fun apply(symbol: Char, key: KeySpec, typePrefix: String): KeySpec = key.copy(
        secondaryLabel = symbol.toString(),
        accessibilityLabel = "${key.accessibilityLabel}, $typePrefix $symbol",
        alternates = listOf(
            KeyAlternate.Text(symbol.toString(), accessibilityLabel = "$typePrefix $symbol"),
        ) + key.alternates,
    )
}
