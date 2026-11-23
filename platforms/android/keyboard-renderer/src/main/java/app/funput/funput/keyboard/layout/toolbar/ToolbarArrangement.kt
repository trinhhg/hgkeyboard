package app.funput.funput.keyboard.layout.toolbar

/**
 * Ordered toolbar sides. Both [leading] and [trailing] are left-to-right: first entry is the
 * leftmost key on that side, last entry is the rightmost (flush with the side's outer edge).
 */
data class ToolbarArrangement(
    val leading: List<ToolbarSlot>,
    val trailing: List<ToolbarSlot>,
) {
    /**
     * Drops unavailable slots, keeps the first occurrence of each role, and restores any missing
     * available role onto its default side so the bar stays complete.
     */
    fun normalize(available: Set<ToolbarSlot>): ToolbarArrangement {
        val seen = linkedSetOf<ToolbarSlot>()
        fun take(source: List<ToolbarSlot>) = source.filter { it in available && seen.add(it) }
        val nextLeading = take(leading).toMutableList()
        val nextTrailing = take(trailing).toMutableList()
        available.forEach { slot ->
            if (!seen.add(slot)) return@forEach
            if (slot in Default.leading) nextLeading += slot else nextTrailing += slot
        }
        return ToolbarArrangement(nextLeading, nextTrailing)
    }

    companion object {
        /** Laban/SwiftKey 6-icon utility toolbar: Mic, Theme, Settings on left; Clipboard, Placement, Emoji on right. */
        val Default = ToolbarArrangement(
            leading = listOf(ToolbarSlot.MICROPHONE, ToolbarSlot.THEME, ToolbarSlot.SETTINGS),
            trailing = listOf(ToolbarSlot.CLIPBOARD, ToolbarSlot.PLACEMENT, ToolbarSlot.EMOJI),
        )
    }
}
