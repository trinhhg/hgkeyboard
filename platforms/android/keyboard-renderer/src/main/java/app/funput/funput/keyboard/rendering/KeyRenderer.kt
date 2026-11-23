package app.funput.funput.keyboard.rendering

import android.graphics.Canvas
import android.graphics.Paint
import app.funput.funput.keyboard.layout.ResolvedKey
import app.funput.funput.keyboard.model.KeyRole
import app.funput.funput.keyboard.model.KeyboardEnterAction
import app.funput.funput.keyboard.model.KeyboardLanguage
import app.funput.funput.keyboard.model.ShiftState
import app.funput.funput.theme.KeyboardTheme

/** Draws key surfaces and text while delegating symbolic key artwork. */
internal class KeyRenderer(private val metrics: RenderMetrics) {
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }
    private val fontMetrics = Paint.FontMetrics()
    private val surfacePainter = KeySurfacePainter(metrics)
    private val iconRenderer = KeyIconRenderer(metrics)
    private val spacebarRenderer = SpacebarContentRenderer(metrics)

    fun updateTheme(theme: KeyboardTheme, width: Int, height: Int) {
        surfacePainter.updateTheme(theme, width, height)
        iconRenderer.updateTheme(theme)
    }

    fun draw(
        canvas: Canvas,
        key: ResolvedKey,
        theme: KeyboardTheme,
        isPressed: Boolean,
        shiftState: ShiftState,
        language: KeyboardLanguage,
        enterAction: KeyboardEnterAction,
        clipboardOfferActive: Boolean = false,
        alwaysCapitalizeLabels: Boolean = false,
    ) {
        if (key.spec.role == KeyRole.PLACEHOLDER) return
        val isActivated = key.active || (key.spec.role == KeyRole.SHIFT && shiftState.isActive)
        surfacePainter.draw(canvas, key, theme, isPressed, isActivated)
        when {
            key.spec.role == KeyRole.SPACE -> spacebarRenderer.draw(
                canvas,
                key,
                theme,
                language,
                customLabel = key.spec.spaceLabelOverride,
            )
            !iconRenderer.draw(canvas, key, shiftState, enterAction, clipboardOfferActive) ->
                drawLabels(canvas, key, theme, shiftState, alwaysCapitalizeLabels)
        }
    }

    private fun drawLabels(
        canvas: Canvas,
        key: ResolvedKey,
        theme: KeyboardTheme,
        shiftState: ShiftState,
        alwaysCapitalizeLabels: Boolean,
    ) {
        val role = key.spec.role
        val secondaryLabel = key.spec.secondaryLabel
        labelPaint.color = if (role.isSpecial) theme.specialLabelColor else theme.labelColor
        labelPaint.textSize = metrics.labelSp(role.labelSizeSp, theme)
        labelPaint.typeface = if (role == KeyRole.CHARACTER || role == KeyRole.VNI_MODIFIER) {
            KeyLabelTypography.characterTypeface(theme)
        } else {
            KeyLabelTypography.specialTypeface(theme)
        }
        labelPaint.textAlign = Paint.Align.CENTER
        val label = if (role == KeyRole.CHARACTER && (shiftState.isActive || alwaysCapitalizeLabels)) {
            key.spec.shiftedLabel ?: key.spec.label
        } else {
            key.spec.label
        }
        labelPaint.shrinkToFit(label, key.bounds.width - metrics.dp(LabelPaddingDp))
        labelPaint.getFontMetrics(fontMetrics)
        val mainVerticalShift = if (theme.centerSecondaryLabel && secondaryLabel != null) {
            metrics.dp(2.5f)
        } else {
            0f
        }
        val baseline = key.bounds.centerY + mainVerticalShift - (fontMetrics.ascent + fontMetrics.descent) / 2f
        canvas.drawText(label, key.bounds.centerX, baseline, labelPaint)

        if (secondaryLabel == null) return
        labelPaint.color = theme.secondaryLabelColor
        labelPaint.textSize = metrics.labelSp(
            if (theme.centerSecondaryLabel) CenteredSecondarySizeSp else SecondaryLabelSizeSp,
            theme,
        )
        labelPaint.getFontMetrics(fontMetrics)
        if (theme.centerSecondaryLabel) {
            labelPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(
                secondaryLabel,
                key.bounds.centerX,
                key.bounds.top + metrics.dp(2.8f) - fontMetrics.ascent,
                labelPaint,
            )
        } else {
            labelPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText(
                secondaryLabel,
                key.bounds.right - metrics.dp(4.5f),
                key.bounds.top + metrics.dp(3.5f) - fontMetrics.ascent,
                labelPaint,
            )
        }
    }

    private val KeyRole.labelSizeSp: Float
        get() = when (this) {
            KeyRole.PLACEHOLDER,
            KeyRole.CHARACTER, KeyRole.VNI_MODIFIER, KeyRole.PUNCTUATION -> CharacterLabelSizeSp
            else -> SpecialLabelSizeSp
        }

    private companion object {
        const val CharacterLabelSizeSp = 20f
        const val SpecialLabelSizeSp = 13f
        const val SecondaryLabelSizeSp = 8.5f
        const val CenteredSecondarySizeSp = 9.5f
        const val LabelPaddingDp = 8f
    }
}
