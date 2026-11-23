package app.funput.funput.keyboard.layout.toolbar

import org.junit.Assert.assertEquals
import org.junit.Test

class ToolbarArrangementTest {
    @Test
    fun `default keeps mic theme settings leading with clipboard placement emoji trailing`() {
        assertEquals(
            listOf(ToolbarSlot.MICROPHONE, ToolbarSlot.THEME, ToolbarSlot.SETTINGS),
            ToolbarArrangement.Default.leading,
        )
        assertEquals(
            listOf(ToolbarSlot.CLIPBOARD, ToolbarSlot.PLACEMENT, ToolbarSlot.EMOJI),
            ToolbarArrangement.Default.trailing,
        )
    }

    @Test
    fun `normalize drops unavailable duplicates and restores missing onto default sides`() {
        val raw = ToolbarArrangement(
            leading = listOf(ToolbarSlot.EMOJI, ToolbarSlot.EMOJI, ToolbarSlot.CLIPBOARD),
            trailing = listOf(ToolbarSlot.MICROPHONE),
            hidden = emptySet(),
        )
        val normalized = raw.normalize(
            setOf(ToolbarSlot.EMOJI, ToolbarSlot.MICROPHONE, ToolbarSlot.PLACEMENT),
        )
        assertEquals(listOf(ToolbarSlot.EMOJI), normalized.leading)
        assertEquals(listOf(ToolbarSlot.MICROPHONE, ToolbarSlot.PLACEMENT), normalized.trailing)
    }
}
