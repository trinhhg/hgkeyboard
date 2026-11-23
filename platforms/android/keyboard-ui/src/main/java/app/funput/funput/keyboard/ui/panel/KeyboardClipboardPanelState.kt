package app.funput.funput.keyboard.ui.panel

import app.funput.funput.keyboard.KeyboardSurfaceView
import app.funput.funput.keyboard.model.KeyboardEditorMode
import app.funput.funput.keyboard.ui.KeyboardPanel
import app.funput.funput.keyboard.ui.clipboard.ClipboardPanelView
import app.funput.funput.keyboard.ui.clipboard.KeyboardClipboardEntry
import app.funput.funput.keyboard.ui.clipboard.KeyboardShortcutEntry

internal class KeyboardClipboardPanelState(
    private val surface: KeyboardSurfaceView,
    private val editorMode: () -> KeyboardEditorMode,
    private val activePanel: () -> KeyboardPanel,
    private val showLetters: () -> Unit,
) {
    private var panel: ClipboardPanelView? = null
    private var pendingTab: Int = 1
    var enabled: Boolean = false
        set(value) {
            field = value
            syncAvailability()
            if (!value && activePanel() == KeyboardPanel.CLIPBOARD) showLetters()
        }
    var entries: List<KeyboardClipboardEntry> = emptyList()
        set(value) { field = value; syncPanel() }
    var loading: Boolean = false
        set(value) { field = value; syncPanel() }
    var shortcuts: List<KeyboardShortcutEntry> = emptyList()
        set(value) { field = value; syncShortcuts() }

    fun attach(value: ClipboardPanelView) {
        panel = value
        value.selectTab(pendingTab)
        syncPanel()
        syncShortcuts()
    }

    fun selectTab(tabIndex: Int) {
        pendingTab = tabIndex
        panel?.selectTab(tabIndex)
    }

    fun editorModeChanged() = syncAvailability()

    fun available(): Boolean = enabled && !editorMode().isPassword && !editorMode().usesKeypad

    private fun syncAvailability() { surface.clipboardKeyVisible = available() }
    private fun syncPanel() { panel?.submit(entries, loading) }
    private fun syncShortcuts() { panel?.submitShortcuts(shortcuts) }
}
