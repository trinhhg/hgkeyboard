package app.funput.funput.keyboard.model

/** Structural layout choices that distinguish Laban Key, SwiftKey, and Glass keyboard layouts. */
data class KeyboardStyleOptions(
    val showSecondarySymbols: Boolean = true,
    val showBottomEmojiKey: Boolean = true,
) {
    companion object {
        val Default = KeyboardStyleOptions()
        val Laban = KeyboardStyleOptions(showSecondarySymbols = false, showBottomEmojiKey = false)
        val SwiftKey = KeyboardStyleOptions(showSecondarySymbols = true, showBottomEmojiKey = true)
    }
}
