package app.funput.funput.keyboard

import app.funput.funput.keyboard.layout.ResolvedKeyboard
import app.funput.funput.keyboard.suggestion.SuggestionChip

/**
 * Normalizes suggestion candidates and manages switching between the 6-icon utility toolbar
 * and the 3-slot prediction band.
 */
internal class KeyboardSurfaceSuggestionState(
    private val density: () -> Float,
    private val keyboard: () -> ResolvedKeyboard?,
    private val apply: (List<SuggestionChip>) -> Unit,
    private val onUtilityKeysVisibilityChanged: (Boolean) -> Unit = {},
) {
    private var requested = emptyList<SuggestionChip>()
    private var toolbarExpanded = false
    private var typingActive = false

    var isDualBar: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            val next = if (value) true else !typingActive
            if (next != utilityKeysVisible) {
                utilityKeysVisible = next
                onUtilityKeysVisibilityChanged(next)
            } else {
                refresh()
            }
        }

    val hasSuggestions: Boolean get() = isDualBar || typingActive || requested.isNotEmpty()

    /** True when the single-bar strip shows 6 utility icons; always true when dual-bar top strip is visible. */
    var utilityKeysVisible: Boolean = true
        private set

    fun update(values: List<SuggestionChip>) {
        requested = SuggestionNormalizer.normalize(values)
        if (isDualBar) {
            refresh()
            return
        }
        if (requested.isNotEmpty()) {
            typingActive = true
            toolbarExpanded = false
            if (utilityKeysVisible) {
                utilityKeysVisible = false
                onUtilityKeysVisibilityChanged(false)
                return
            }
        }
        // When typingActive is true and requested becomes transiently empty (e.g. between VNI
        // digits or Spacebar), NEVER flip utilityKeysVisible back to true — prevents toolbar flicker!
        refresh()
    }

    fun updateTypingPresence(hasTextBeforeCursor: Boolean) {
        if (isDualBar) {
            typingActive = hasTextBeforeCursor
            refresh()
            return
        }
        if (hasTextBeforeCursor) {
            if (!typingActive) {
                typingActive = true
                if (!toolbarExpanded && utilityKeysVisible) {
                    utilityKeysVisible = false
                    onUtilityKeysVisibilityChanged(false)
                }
            }
        } else if (requested.isEmpty()) {
            typingActive = false
            toolbarExpanded = false
            if (!utilityKeysVisible) {
                utilityKeysVisible = true
                onUtilityKeysVisibilityChanged(true)
            }
        }
    }

    fun toggleToolbar() {
        val next = !utilityKeysVisible
        toolbarExpanded = next
        utilityKeysVisible = next
        onUtilityKeysVisibilityChanged(next)
    }

    fun geometryChanged() = refresh()

    private fun refresh() {
        val width = keyboard()?.suggestionBar?.suggestionsBounds?.width ?: return
        if (width <= 0f) {
            apply(emptyList())
            return
        }
        val count = SuggestionCapacity.visibleCount(width, density(), requested.size)
        apply(requested.take(count))
    }
}
