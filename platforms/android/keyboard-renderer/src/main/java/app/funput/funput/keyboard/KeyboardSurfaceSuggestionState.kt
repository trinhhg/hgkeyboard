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

    val hasSuggestions: Boolean get() = requested.isNotEmpty()

    /** True when the strip shows the full utility toolbar instead of suggestions. */
    var utilityKeysVisible: Boolean = true
        private set

    fun update(values: List<SuggestionChip>) {
        requested = SuggestionNormalizer.normalize(values)
        // New suggestions arrive as the user types -> automatically show predictions.
        toolbarExpanded = false
        val nextUtilityKeysVisible = requested.isEmpty()
        if (nextUtilityKeysVisible != utilityKeysVisible) {
            utilityKeysVisible = nextUtilityKeysVisible
            onUtilityKeysVisibilityChanged(nextUtilityKeysVisible)
            return
        }
        refresh()
    }

    fun toggleToolbar() {
        if (requested.isEmpty()) return
        toolbarExpanded = !toolbarExpanded
        val nextUtilityKeysVisible = toolbarExpanded
        if (nextUtilityKeysVisible != utilityKeysVisible) {
            utilityKeysVisible = nextUtilityKeysVisible
            onUtilityKeysVisibilityChanged(nextUtilityKeysVisible)
        }
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
