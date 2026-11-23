package app.funput.funput.keyboard.model

import app.funput.funput.keyboard.popover.model.KeyAlternate

data class KeySpec(
    val id: String,
    val label: String,
    val role: KeyRole,
    val widthWeight: Float = 1f,
    val shiftedLabel: String? = null,
    val secondaryLabel: String? = null,
    val accessibilityLabel: String = label,
    val horizontalSwipeAction: KeySwipeAction? = null,
    val spaceLabelOverride: String? = null,
    val alternates: List<KeyAlternate> = emptyList(),
    val preferredAlternateText: String? = null,
    val alternatePaletteColumns: Int? = null,
) {
    init {
        require(id.isNotBlank()) { "Key id must not be blank" }
        require(widthWeight > 0f) { "Key width weight must be positive" }
        require(shiftedLabel == null || shiftedLabel.isNotEmpty()) { "Shifted label must not be empty" }
        require(accessibilityLabel.isNotBlank()) { "Accessibility label must not be blank" }
        require(spaceLabelOverride == null || spaceLabelOverride.isNotBlank()) {
            "Space label override must not be blank"
        }
        require(preferredAlternateText == null || alternates.any { it is KeyAlternate.Text && it.text == preferredAlternateText }) {
            "Preferred alternate must belong to the key"
        }
        require(alternatePaletteColumns == null || alternatePaletteColumns > 0) {
            "Alternate palette columns must be positive"
        }
    }
}

enum class KeySwipeAction {
    TOGGLE_LANGUAGE,
}

data class KeyboardRow(
    val keys: List<KeySpec>,
    val horizontalInsetUnits: Float = 0f,
) {
    init {
        require(keys.isNotEmpty()) { "Keyboard row must contain at least one key" }
        require(horizontalInsetUnits >= 0f) { "Row inset must not be negative" }
        require(keys.map(KeySpec::id).distinct().size == keys.size) { "Key ids must be unique within a row" }
    }
}

data class SuggestionBarSpec(
    val emojiKey: KeySpec,
    val placementKey: KeySpec,
    val clipboardKey: KeySpec? = null,
    val systemInputMethodKey: KeySpec? = null,
    val suggestionsEnabled: Boolean = true,
    val microphoneKey: KeySpec? = null,
    val themeKey: KeySpec? = null,
    val settingsKey: KeySpec? = null,
    val numpadKey: KeySpec? = null,
    val shortcutsKey: KeySpec? = null,
    val toolbarToggleKey: KeySpec? = null,
) {
    init {
        require(microphoneKey == null || microphoneKey.role == KeyRole.MICROPHONE) {
            "Toolbar microphone action must be a microphone key"
        }
        require(emojiKey.role == KeyRole.EMOJI) { "Toolbar emoji action must be an emoji key" }
        require(placementKey.role == KeyRole.PLACEMENT) { "Toolbar placement action has the wrong role" }
        require(clipboardKey == null || clipboardKey.role == KeyRole.CLIPBOARD) {
            "Toolbar clipboard action must be a clipboard key"
        }
        require(systemInputMethodKey == null || systemInputMethodKey.role == KeyRole.SYSTEM_INPUT_METHOD) {
            "Toolbar input-method action must be a system input-method key"
        }
        require(themeKey == null || themeKey.role == KeyRole.THEME) {
            "Toolbar theme action must be a theme key"
        }
        require(settingsKey == null || settingsKey.role == KeyRole.SETTINGS) {
            "Toolbar settings action must be a settings key"
        }
        require(numpadKey == null || numpadKey.role == KeyRole.NUMPAD) {
            "Toolbar numpad action must be a numpad key"
        }
        require(shortcutsKey == null || shortcutsKey.role == KeyRole.SHORTCUTS) {
            "Toolbar shortcuts action must be a shortcuts key"
        }
        require(toolbarToggleKey == null || toolbarToggleKey.role == KeyRole.TOOLBAR_TOGGLE) {
            "Toolbar toggle action must be a toggle key"
        }
    }
}

data class KeyboardLayout(
    val id: String,
    val inputMethod: KeyboardInputMethod,
    val suggestionBar: SuggestionBarSpec?,
    val rows: List<KeyboardRow>,
) {
    init {
        require(id.isNotBlank()) { "Layout id must not be blank" }
        require(rows.isNotEmpty()) { "Keyboard layout must contain at least one row" }

        val keyIds = buildList {
            suggestionBar?.toolbarToggleKey?.let { add(it.id) }
            suggestionBar?.clipboardKey?.let { add(it.id) }
            suggestionBar?.microphoneKey?.let { add(it.id) }
            suggestionBar?.themeKey?.let { add(it.id) }
            suggestionBar?.settingsKey?.let { add(it.id) }
            suggestionBar?.numpadKey?.let { add(it.id) }
            suggestionBar?.shortcutsKey?.let { add(it.id) }
            suggestionBar?.emojiKey?.let { add(it.id) }
            suggestionBar?.placementKey?.let { add(it.id) }
            suggestionBar?.systemInputMethodKey?.let { add(it.id) }
            rows.forEach { row -> addAll(row.keys.map(KeySpec::id)) }
        }
        require(keyIds.distinct().size == keyIds.size) { "Key ids must be unique within a layout" }
    }
}
