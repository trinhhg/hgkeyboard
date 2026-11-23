package app.funput.funput.keyboard.layout.geometry

import app.funput.funput.keyboard.layout.KeyBounds
import app.funput.funput.keyboard.layout.ResolvedKey
import app.funput.funput.keyboard.layout.ResolvedKeyboard
import app.funput.funput.keyboard.layout.ResolvedSuggestionBar

/**
 * Expands visual key bounds to gap-free touch targets.
 *
 * Adjacent targets meet halfway through each visual gap. Outer keys extend to the surface edge,
 * matching the forgiving hit behavior users expect from a software keyboard.
 */
internal object KeyboardHitTargetResolver {
    fun resolve(keyboard: ResolvedKeyboard): ResolvedKeyboard {
        val suggestionBar = keyboard.suggestionBar?.let { bar -> resolveSuggestionBar(keyboard, bar) }
        val rows = keyboard.rows.mapIndexed { rowIndex, row ->
            val hitTop = rowHitTop(keyboard, rowIndex)
            val hitBottom = rowHitBottom(keyboard, rowIndex)
            row.mapIndexed { keyIndex, key ->
                key.copy(
                    hitBounds = KeyBounds(
                        left = keyHitLeft(row, keyIndex),
                        top = hitTop,
                        right = keyHitRight(row, keyIndex, keyboard.width),
                        bottom = hitBottom,
                    ),
                )
            }
        }
        return keyboard.copy(suggestionBar = suggestionBar, rows = rows)
    }

    private fun resolveSuggestionBar(
        keyboard: ResolvedKeyboard,
        bar: ResolvedSuggestionBar,
    ): ResolvedSuggestionBar {
        val controls = listOfNotNull(
            bar.toolbarToggleKey, bar.microphoneKey, bar.themeKey, bar.settingsKey,
            bar.placementKey, bar.systemInputMethodKey, bar.clipboardKey, bar.emojiKey,
        ).sortedBy { it.bounds.left }
        val hitBottom = midpoint(bar.bounds.bottom, keyboard.rows.first().first().bounds.top)
        val toolbarRow = bar.toolbarRowBounds

        if (toolbarRow == null) {
            val spans = (
                controls.map { it.bounds } +
                    listOfNotNull(bar.suggestionsBounds.takeIf { bar.suggestionsEnabled })
                ).sortedBy { it.left }
            fun edges(bounds: KeyBounds): Pair<Float, Float> {
                val index = spans.indexOf(bounds)
                val left = spans.getOrNull(index - 1)?.let { midpoint(it.right, bounds.left) } ?: 0f
                val right = spans.getOrNull(index + 1)?.let { midpoint(bounds.right, it.left) } ?: keyboard.width
                return left to right
            }
            val resolvedControls = controls.map { key ->
                val (left, right) = edges(key.bounds)
                resolveToolbarKey(key, left, 0f, right, hitBottom)
            }
            fun lookup(key: ResolvedKey?): ResolvedKey? =
                key?.let { target -> resolvedControls.first { it.spec.id == target.spec.id } }
            val suggestionEdges = if (bar.suggestionsEnabled) edges(bar.suggestionsBounds)
                else bar.suggestionsBounds.left to bar.suggestionsBounds.right
            return bar.copy(
                suggestionsHitBounds = KeyBounds(
                    left = suggestionEdges.first,
                    top = 0f,
                    right = suggestionEdges.second,
                    bottom = hitBottom,
                ),
                toolbarToggleKey = lookup(bar.toolbarToggleKey),
                systemInputMethodKey = lookup(bar.systemInputMethodKey),
                clipboardKey = lookup(bar.clipboardKey),
                placementKey = lookup(bar.placementKey),
                microphoneKey = lookup(bar.microphoneKey),
                themeKey = lookup(bar.themeKey),
                settingsKey = lookup(bar.settingsKey),
                emojiKey = requireNotNull(lookup(bar.emojiKey)),
            )
        }

        // Dual-bar mode (SwiftKey): top utility strip and lower suggestion strip never share Y-bounds.
        val rowBoundaryY = midpoint(toolbarRow.bottom, bar.suggestionsBounds.top)
        val row1Controls = controls.filter { it.bounds.centerY < rowBoundaryY }.sortedBy { it.bounds.left }
        val row2Controls = controls.filter { it.bounds.centerY >= rowBoundaryY }.sortedBy { it.bounds.left }
        val row2Spans = (
            row2Controls.map { it.bounds } +
                listOfNotNull(bar.suggestionsBounds.takeIf { bar.suggestionsEnabled })
            ).sortedBy { it.left }

        fun rowEdges(list: List<KeyBounds>, bounds: KeyBounds): Pair<Float, Float> {
            val index = list.indexOf(bounds)
            val left = list.getOrNull(index - 1)?.let { midpoint(it.right, bounds.left) } ?: 0f
            val right = list.getOrNull(index + 1)?.let { midpoint(bounds.right, it.left) } ?: keyboard.width
            return left to right
        }

        val resolvedControls = buildList {
            val row1Bounds = row1Controls.map { it.bounds }
            row1Controls.forEach { key ->
                val (left, right) = rowEdges(row1Bounds, key.bounds)
                add(resolveToolbarKey(key, left, 0f, right, rowBoundaryY))
            }
            row2Controls.forEach { key ->
                val (left, right) = rowEdges(row2Spans, key.bounds)
                add(resolveToolbarKey(key, left, rowBoundaryY, right, hitBottom))
            }
        }
        fun lookup(key: ResolvedKey?): ResolvedKey? =
            key?.let { target -> resolvedControls.first { it.spec.id == target.spec.id } }
        val suggestionEdges = if (bar.suggestionsEnabled) rowEdges(row2Spans, bar.suggestionsBounds)
            else bar.suggestionsBounds.left to bar.suggestionsBounds.right
        return bar.copy(
            suggestionsHitBounds = KeyBounds(
                left = suggestionEdges.first,
                top = rowBoundaryY,
                right = suggestionEdges.second,
                bottom = hitBottom,
            ),
            toolbarToggleKey = lookup(bar.toolbarToggleKey),
            systemInputMethodKey = lookup(bar.systemInputMethodKey),
            clipboardKey = lookup(bar.clipboardKey),
            placementKey = lookup(bar.placementKey),
            microphoneKey = lookup(bar.microphoneKey),
            themeKey = lookup(bar.themeKey),
            settingsKey = lookup(bar.settingsKey),
            emojiKey = requireNotNull(lookup(bar.emojiKey)),
        )
    }

    private fun resolveToolbarKey(
        key: ResolvedKey,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
    ): ResolvedKey = key.copy(
        hitBounds = KeyBounds(left = left, top = top, right = right, bottom = bottom),
    )

    private fun rowHitTop(keyboard: ResolvedKeyboard, rowIndex: Int): Float {
        val row = keyboard.rows[rowIndex]
        return if (rowIndex == 0) {
            keyboard.suggestionBar?.let { midpoint(it.bounds.bottom, row.first().bounds.top) } ?: 0f
        } else {
            midpoint(keyboard.rows[rowIndex - 1].first().bounds.bottom, row.first().bounds.top)
        }
    }

    private fun rowHitBottom(keyboard: ResolvedKeyboard, rowIndex: Int): Float {
        val row = keyboard.rows[rowIndex]
        return if (rowIndex == keyboard.rows.lastIndex) {
            keyboard.height
        } else {
            midpoint(row.first().bounds.bottom, keyboard.rows[rowIndex + 1].first().bounds.top)
        }
    }

    private fun keyHitLeft(row: List<ResolvedKey>, keyIndex: Int): Float = if (keyIndex == 0) {
        0f
    } else {
        midpoint(row[keyIndex - 1].bounds.right, row[keyIndex].bounds.left)
    }

    private fun keyHitRight(
        row: List<ResolvedKey>,
        keyIndex: Int,
        keyboardWidth: Float,
    ): Float = if (keyIndex == row.lastIndex) {
        keyboardWidth
    } else {
        midpoint(row[keyIndex].bounds.right, row[keyIndex + 1].bounds.left)
    }

    private fun midpoint(first: Float, second: Float): Float = (first + second) / 2f
}
