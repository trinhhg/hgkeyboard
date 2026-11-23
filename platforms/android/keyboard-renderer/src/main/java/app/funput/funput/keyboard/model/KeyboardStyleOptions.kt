package app.funput.funput.keyboard.model

/** How the top toolbar + suggestion area behaves. */
enum class KeyboardToolbarMode {
    AUTO_PRESET,
    SINGLE_BAR,
    DUAL_BAR,
}

/** What horizontal swipe or hold on the spacebar does (Laban Key style). */
enum class SpaceSwipeBehavior {
    MOVE_CURSOR,
    SWITCH_LANGUAGE,
}

/** Structural layout choices that distinguish Laban Key, SwiftKey, and Glass keyboard layouts. */
data class KeyboardStyleOptions(
    val showSecondarySymbols: Boolean = true,
    val showBottomEmojiKey: Boolean = true,
    val showVniAccentsOnNumberRow: Boolean = true,
    val alwaysCapitalizeLabels: Boolean = false,
    val showCommaPeriodKeys: Boolean = true,
    val showLanguageSwitchKey: Boolean = false,
    val spaceSwipeSwitchesLanguage: Boolean = false,
    val toolbarMode: KeyboardToolbarMode = KeyboardToolbarMode.AUTO_PRESET,
    val isDualBar: Boolean = false,
) {
    companion object {
        val Default = KeyboardStyleOptions()
        val Laban = KeyboardStyleOptions(
            showSecondarySymbols = false,
            showBottomEmojiKey = false,
            isDualBar = false,
        )
        val SwiftKey = KeyboardStyleOptions(
            showSecondarySymbols = true,
            showBottomEmojiKey = true,
            isDualBar = true,
        )
    }
}
