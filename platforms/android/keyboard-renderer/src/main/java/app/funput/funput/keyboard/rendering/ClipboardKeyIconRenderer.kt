package app.funput.funput.keyboard.rendering

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import app.funput.funput.keyboard.layout.ResolvedKey
import app.funput.funput.theme.KeyboardTheme
import kotlin.math.min

/** Two stacked frames (copy) with an accent badge when clipboard content is offered. */
internal class ClipboardKeyIconRenderer(private val metrics: RenderMetrics) {
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val back = RectF()
    private val front = RectF()
    private var accentColor: Int = 0

    fun updateTheme(theme: KeyboardTheme) {
        stroke.color = theme.specialLabelColor
        stroke.strokeWidth = metrics.dp(1.45f)
        accentColor = theme.accentColor
    }

    fun draw(canvas: Canvas, key: ResolvedKey, offerActive: Boolean) {
        val size = min(
            min(key.bounds.width, key.bounds.height) * IconSizeRatio,
            metrics.dp(MaxIconHeightDp),
        )
        val w = size * 0.78f
        val h = size * 0.94f
        val radius = metrics.dp(CornerRadiusDp)
        val cx = key.bounds.centerX
        val cy = key.bounds.centerY + h * 0.06f
        val left = cx - w / 2f
        val top = cy - h / 2f
        val right = cx + w / 2f
        val bottom = cy + h / 2f
        // SwiftKey-style clipboard board with top clip bar and 2 horizontal lines.
        back.set(left, top, right, bottom)
        canvas.drawRoundRect(back, radius, radius, stroke)
        front.set(cx - w * 0.28f, top - h * 0.14f, cx + w * 0.28f, top + h * 0.10f)
        canvas.drawRoundRect(front, radius * 0.6f, radius * 0.6f, stroke)
        canvas.drawLine(left + w * 0.26f, cy - h * 0.04f, right - w * 0.26f, cy - h * 0.04f, stroke)
        canvas.drawLine(left + w * 0.26f, cy + h * 0.22f, right - w * 0.34f, cy + h * 0.22f, stroke)
        if (offerActive) drawBadge(canvas, key)
    }

    private fun drawBadge(canvas: Canvas, key: ResolvedKey) {
        val radius = metrics.dp(BadgeRadiusDp)
        fill.color = accentColor
        canvas.drawCircle(
            key.bounds.right - metrics.dp(BadgeInsetDp) - radius,
            key.bounds.top + metrics.dp(BadgeInsetDp) + radius,
            radius,
            fill,
        )
    }

    private companion object {
        const val IconSizeRatio = 0.42f
        const val MaxIconHeightDp = 19f
        const val CornerRadiusDp = 2f
        const val BadgeRadiusDp = 2.25f
        const val BadgeInsetDp = 5f
    }
}
