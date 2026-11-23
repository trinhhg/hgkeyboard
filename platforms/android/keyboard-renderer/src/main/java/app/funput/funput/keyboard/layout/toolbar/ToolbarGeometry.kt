package app.funput.funput.keyboard.layout.toolbar

import app.funput.funput.keyboard.layout.KeyBounds
import app.funput.funput.keyboard.layout.KeyboardGeometrySpec
import app.funput.funput.keyboard.layout.ResolvedKey
import app.funput.funput.keyboard.layout.ResolvedSuggestionBar
import app.funput.funput.keyboard.model.KeyboardLayout
import app.funput.funput.keyboard.model.SuggestionBarSpec
import app.funput.funput.keyboard.utility.KeyboardMicrophoneState

/**
 * Places toolbar slots from [ToolbarArrangement]:
 * - In toolbar mode ([showUtilities] == true): spreads all utility actions cleanly across the bar
 *   like Laban Key / SwiftKey.
 * - In suggestion mode ([showUtilities] == false): gives suggestions maximum width with a leading
 *   expand-toolbar toggle on the left and quick emoji on the right.
 */
internal object ToolbarGeometry {
    private const val TooNarrow = "Keyboard is too narrow for its required toolbar actions"

    fun resolve(
        layout: KeyboardLayout,
        width: Float,
        spec: KeyboardGeometrySpec,
        showClipboard: Boolean = false,
        showPlacement: Boolean = true,
        microphone: KeyboardMicrophoneState = KeyboardMicrophoneState(),
        arrangement: ToolbarArrangement = ToolbarArrangement.Default,
        showUtilities: Boolean = true,
        hasSuggestions: Boolean = false,
    ): ResolvedSuggestionBar? {
        val bar = layout.suggestionBar ?: return null
        val top = spec.verticalPadding
        val bottom = top + spec.suggestionBarHeight
        val cell = spec.suggestionBarHeight
        val ordered = arrangement.normalize(
            availableSlots(bar, showClipboard, showPlacement, microphone.visible, showUtilities),
        )
        val placed = linkedMapOf<ToolbarSlot, KeyBounds>()
        var left = spec.horizontalPadding
        var right = width - spec.horizontalPadding

        val toggleKey = if (hasSuggestions && bar.toolbarToggleKey != null) {
            val toggleWidth = (cell * 0.78f).coerceAtMost((right - left) * 0.25f)
            val toggleBounds = KeyBounds(left, top, left + toggleWidth, bottom)
            left = toggleBounds.right + spec.horizontalGap
            ResolvedKey(bar.toolbarToggleKey, toggleBounds, active = showUtilities)
        } else {
            null
        }

        val allSlots = ordered.leading + ordered.trailing
        if (showUtilities && allSlots.isNotEmpty()) {
            // Evenly space utility slots across the full strip width, matching Laban Key & SwiftKey toolbars.
            val availableWidth = (right - left).coerceAtLeast(cell)
            val maxItems = ((availableWidth + spec.horizontalGap) / (cell * 0.85f + spec.horizontalGap)).toInt().coerceAtLeast(1)
            val visibleSlots = trimOptionalSlots(allSlots, maxItems)
            val step = availableWidth / visibleSlots.size
            val slotWidth = cell.coerceAtMost(step)
            visibleSlots.forEachIndexed { index, slot ->
                val center = left + step * (index + 0.5f)
                placed[slot] = KeyBounds(center - slotWidth / 2f, top, center + slotWidth / 2f, bottom)
            }
            val suggestionsCenter = (left + right) / 2f
            return buildResult(
                bar = bar,
                bounds = KeyBounds(spec.horizontalPadding, top, width - spec.horizontalPadding, bottom),
                suggestionsBounds = KeyBounds(suggestionsCenter, top, suggestionsCenter, bottom),
                placed = placed,
                systemBounds = null,
                toggleKey = toggleKey,
                microphone = microphone,
            )
        }

        val pending = allSlots.toMutableList()
        // Last trailing entry is flush right; list order stays left-to-right on screen.
        ordered.trailing.asReversed().forEachIndexed { index, slot ->
            placeTrailing(slot.yieldsWhenNarrow, index == 0, cell, top, bottom, spec, left, right, reserve(pending, slot, cell, spec.horizontalGap))
                ?.let { placed[slot] = it; right = it.left; pending.remove(slot) }
        }
        val systemBounds = bar.systemInputMethodKey?.let {
            placeTrailing(true, false, cell, top, bottom, spec, left, right, reserveRequired(pending, cell, spec.horizontalGap))
                ?.also { right = it.left }
        }
        ordered.leading.forEach { slot ->
            placeLeading(slot, cell, top, bottom, spec, left, right, reserve(pending, slot, cell, spec.horizontalGap))
                ?.let { placed[slot] = it; left = it.right + spec.horizontalGap; pending.remove(slot) }
        }
        require(ToolbarSlot.EMOJI in placed) { TooNarrow }
        val suggestionsLeft = left.coerceAtMost(right)
        val suggestionsRight = (right - if (bar.suggestionsEnabled) spec.horizontalGap else 0f).coerceAtLeast(suggestionsLeft)
        return buildResult(
            bar = bar,
            bounds = KeyBounds(spec.horizontalPadding, top, width - spec.horizontalPadding, bottom),
            suggestionsBounds = KeyBounds(suggestionsLeft, top, suggestionsRight, bottom),
            placed = placed,
            systemBounds = systemBounds,
            toggleKey = toggleKey,
            microphone = microphone,
        )
    }

    private fun buildResult(
        bar: SuggestionBarSpec,
        bounds: KeyBounds,
        suggestionsBounds: KeyBounds,
        placed: Map<ToolbarSlot, KeyBounds>,
        systemBounds: KeyBounds?,
        toggleKey: ResolvedKey?,
        microphone: KeyboardMicrophoneState,
    ): ResolvedSuggestionBar = ResolvedSuggestionBar(
        bounds = bounds,
        suggestionsBounds = suggestionsBounds,
        systemInputMethodKey = systemBounds?.let { ResolvedKey(requireNotNull(bar.systemInputMethodKey), it) },
        clipboardKey = placed[ToolbarSlot.CLIPBOARD]?.let { ResolvedKey(requireNotNull(bar.clipboardKey), it) },
        placementKey = placed[ToolbarSlot.PLACEMENT]?.let { ResolvedKey(bar.placementKey, it) },
        emojiKey = ResolvedKey(bar.emojiKey, requireNotNull(placed[ToolbarSlot.EMOJI])),
        microphoneKey = placed[ToolbarSlot.MICROPHONE]?.let {
            ResolvedKey(requireNotNull(bar.microphoneKey).copy(accessibilityLabel = microphone.accessibilityLabel), it, active = microphone.active)
        },
        themeKey = placed[ToolbarSlot.THEME]?.let { ResolvedKey(requireNotNull(bar.themeKey), it) },
        settingsKey = placed[ToolbarSlot.SETTINGS]?.let { ResolvedKey(requireNotNull(bar.settingsKey), it) },
        toolbarToggleKey = toggleKey,
        suggestionsEnabled = bar.suggestionsEnabled,
    )

    private fun trimOptionalSlots(slots: List<ToolbarSlot>, maxItems: Int): List<ToolbarSlot> {
        if (slots.size <= maxItems) return slots
        val kept = slots.toMutableList()
        for (candidate in slots) {
            if (kept.size <= maxItems) break
            if (candidate.yieldsWhenNarrow) kept.remove(candidate)
        }
        return kept.take(maxItems)
    }

    private fun reserve(pending: List<ToolbarSlot>, slot: ToolbarSlot, cell: Float, gap: Float) =
        if (slot.yieldsWhenNarrow) reserveRequired(pending.filter { it != slot }, cell, gap) else 0f

    private fun reserveRequired(pending: List<ToolbarSlot>, cell: Float, gap: Float): Float {
        val n = pending.count { !it.yieldsWhenNarrow }
        return if (n == 0) 0f else n * cell + (n - 1).coerceAtLeast(0) * gap
    }

    private fun availableSlots(
        bar: SuggestionBarSpec,
        showClipboard: Boolean,
        showPlacement: Boolean,
        microphoneVisible: Boolean,
        showUtilities: Boolean,
    ) = buildSet {
        add(ToolbarSlot.EMOJI)
        if (microphoneVisible && bar.microphoneKey != null) add(ToolbarSlot.MICROPHONE)
        if (showUtilities && bar.themeKey != null) add(ToolbarSlot.THEME)
        if (showUtilities && bar.settingsKey != null) add(ToolbarSlot.SETTINGS)
        if (showPlacement) add(ToolbarSlot.PLACEMENT)
        if (showClipboard && bar.clipboardKey != null) add(ToolbarSlot.CLIPBOARD)
    }

    private fun placeLeading(
        slot: ToolbarSlot, cell: Float, top: Float, bottom: Float, spec: KeyboardGeometrySpec,
        leftAnchor: Float, rightAnchor: Float, reserve: Float,
    ): KeyBounds? {
        val bounds = KeyBounds(leftAnchor, top, leftAnchor + cell, bottom)
        val limit = rightAnchor - reserve
        if (bounds.right + spec.horizontalGap > limit && slot.yieldsWhenNarrow) return null
        require(bounds.right <= limit + 0.01f || !slot.yieldsWhenNarrow) { TooNarrow }
        return bounds.takeUnless { it.right > limit + 0.01f }
    }

    private fun placeTrailing(
        yields: Boolean, flush: Boolean, cell: Float, top: Float, bottom: Float, spec: KeyboardGeometrySpec,
        leftAnchor: Float, rightAnchor: Float, reserve: Float,
    ): KeyBounds? {
        val keyRight = rightAnchor - if (flush) 0f else spec.horizontalGap
        val bounds = KeyBounds(keyRight - cell, top, keyRight, bottom)
        val limit = leftAnchor + reserve
        if (bounds.left < limit + spec.horizontalGap && yields) return null
        require(bounds.left >= limit - 0.01f || !yields) { TooNarrow }
        return bounds.takeUnless { it.left < limit - 0.01f }
    }
}
