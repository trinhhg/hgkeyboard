package app.funput.funput.keyboard.layout.toolbar

import app.funput.funput.keyboard.layout.KeyboardGeometry
import app.funput.funput.keyboard.layout.geometry.KeyboardHitTargetResolver

/**
 * Density-independent toolbar geometry, shared by the layout, the renderer and the panel
 * height budget.
 *
 * One copy on purpose: [KeyboardDimensions][app.funput.funput.keyboard.KeyboardDimensions]
 * reserves this strip when it measures the panel and [KeyboardGeometry] lays the band out
 * inside it. Two copies that drift leave the keyboard either squeezing its rows or showing
 * a blank gap above them.
 */
internal object ToolbarMetrics {
    /** Single-bar height matching Laban Key (`suggestions_strip_height = 44dp` / compact 42dp). */
    const val SuggestionBarHeightDp = 42f

    /** Separates the band from the first key row. */
    const val SuggestionBarGapDp = 4f

    /** The vertical strip the single-bar toolbar claims in total. */
    const val ChromeDp = SuggestionBarHeightDp + SuggestionBarGapDp

    /** SwiftKey dual-bar metrics: dedicated top utility strip + suggestion strip underneath. */
    const val DualUtilityRowHeightDp = 36f
    const val DualRowGapDp = 2f
    const val DualSuggestionRowHeightDp = 36f
    const val DualChromeDp = DualUtilityRowHeightDp + DualRowGapDp + DualSuggestionRowHeightDp + SuggestionBarGapDp
}
