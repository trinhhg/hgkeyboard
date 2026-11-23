package app.funput.funput.keyboard.ui

import app.funput.funput.keyboard.model.KeyboardLayoutMode

internal class KeyboardPanelController {
    var activePanel: KeyboardPanel = KeyboardPanel.LETTERS
        private set

    fun show(panel: KeyboardPanel): Boolean {
        if (panel == activePanel) return false
        activePanel = panel
        return true
    }

    fun showSymbols(mode: KeyboardLayoutMode) {
        require(mode != KeyboardLayoutMode.LETTERS) { "Symbols panel requires a non-letters layout" }
        activePanel = KeyboardPanel.SYMBOLS
    }
}
