package app.funput.funput.ui.settings.keyboard.toolbar

import androidx.annotation.StringRes
import app.funput.funput.R
import app.funput.funput.keyboard.layout.toolbar.ToolbarSlot
import app.funput.funput.ui.kit.icons.FunputIcons

/** Titles and icons for each toolbar slot in the layout settings list. */
internal object ToolbarSlotLabels {
    @StringRes
    fun title(slot: ToolbarSlot): Int = when (slot) {
        ToolbarSlot.MICROPHONE -> R.string.settings_toolbar_slot_microphone
        ToolbarSlot.THEME -> R.string.settings_toolbar_slot_theme
        ToolbarSlot.SETTINGS -> R.string.settings_toolbar_slot_settings
        ToolbarSlot.PLACEMENT -> R.string.settings_keyboard_mode_title
        ToolbarSlot.CLIPBOARD -> R.string.settings_toolbar_slot_clipboard
        ToolbarSlot.NUMPAD -> R.string.settings_toolbar_slot_numpad
        ToolbarSlot.SHORTCUTS -> R.string.settings_toolbar_slot_shortcuts
        ToolbarSlot.EMOJI -> R.string.settings_toolbar_slot_emoji
    }

    fun icon(slot: ToolbarSlot): Int = when (slot) {
        ToolbarSlot.MICROPHONE -> FunputIcons.Microphone
        ToolbarSlot.THEME -> FunputIcons.Appearance
        ToolbarSlot.SETTINGS -> FunputIcons.KeySize
        ToolbarSlot.PLACEMENT -> FunputIcons.Placement
        ToolbarSlot.CLIPBOARD -> FunputIcons.Clipboard
        ToolbarSlot.NUMPAD -> FunputIcons.NumberRow
        ToolbarSlot.SHORTCUTS -> FunputIcons.Shortcuts
        ToolbarSlot.EMOJI -> FunputIcons.Suggestions
    }
}
