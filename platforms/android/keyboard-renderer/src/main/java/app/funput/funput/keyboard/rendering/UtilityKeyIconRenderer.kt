package app.funput.funput.keyboard.rendering

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import app.funput.funput.keyboard.layout.ResolvedKey
import app.funput.funput.theme.KeyboardTheme
import kotlin.math.min

internal class UtilityKeyIconRenderer(private val metrics: RenderMetrics) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val path = Path()
    private val drawingRect = RectF()
    private val placement = PlacementKeyIconRenderer(metrics)

    fun updateTheme(theme: KeyboardTheme) {
        paint.color = theme.specialLabelColor
        paint.strokeWidth = metrics.dp(1.7f)
    }

    fun drawBackspace(canvas: Canvas, key: ResolvedKey) {
        val iconWidth = min(key.bounds.width * 0.46f, metrics.dp(25f))
        val iconHeight = iconWidth * 0.62f
        val left = key.bounds.centerX - iconWidth / 2f
        val right = key.bounds.centerX + iconWidth / 2f
        val top = key.bounds.centerY - iconHeight / 2f
        val bottom = key.bounds.centerY + iconHeight / 2f
        path.reset()
        path.moveTo(left, key.bounds.centerY)
        path.lineTo(left + iconHeight * 0.45f, top)
        path.lineTo(right, top)
        path.lineTo(right, bottom)
        path.lineTo(left + iconHeight * 0.45f, bottom)
        path.close()
        canvas.drawPath(path, paint)

        val crossCenter = right - iconHeight * 0.48f
        val crossRadius = iconHeight * 0.18f
        canvas.drawLine(
            crossCenter - crossRadius,
            key.bounds.centerY - crossRadius,
            crossCenter + crossRadius,
            key.bounds.centerY + crossRadius,
            paint,
        )
        canvas.drawLine(
            crossCenter + crossRadius,
            key.bounds.centerY - crossRadius,
            crossCenter - crossRadius,
            key.bounds.centerY + crossRadius,
            paint,
        )
    }

    fun drawSystemInputMethod(canvas: Canvas, key: ResolvedKey) {
        val radius = min(key.bounds.width, key.bounds.height) * 0.2f
        val centerX = key.bounds.centerX
        val centerY = key.bounds.centerY
        drawingRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        canvas.drawOval(drawingRect, paint)
        canvas.drawOval(
            centerX - radius * 0.45f,
            centerY - radius,
            centerX + radius * 0.45f,
            centerY + radius,
            paint,
        )
        canvas.drawLine(centerX - radius, centerY, centerX + radius, centerY, paint)
    }

    fun drawEmoji(canvas: Canvas, key: ResolvedKey) {
        val radius = min(key.bounds.width, key.bounds.height) * 0.19f
        drawingRect.set(
            key.bounds.centerX - radius,
            key.bounds.centerY - radius,
            key.bounds.centerX + radius,
            key.bounds.centerY + radius,
        )
        canvas.drawOval(drawingRect, paint)

        val eyeRadius = radius * 0.07f
        paint.style = Paint.Style.FILL
        canvas.drawCircle(
            key.bounds.centerX - radius * 0.34f,
            key.bounds.centerY - radius * 0.24f,
            eyeRadius,
            paint,
        )
        canvas.drawCircle(
            key.bounds.centerX + radius * 0.34f,
            key.bounds.centerY - radius * 0.24f,
            eyeRadius,
            paint,
        )
        paint.style = Paint.Style.STROKE
        drawingRect.set(
            key.bounds.centerX - radius * 0.48f,
            key.bounds.centerY - radius * 0.06f,
            key.bounds.centerX + radius * 0.48f,
            key.bounds.centerY + radius * 0.5f,
        )
        canvas.drawArc(drawingRect, 18f, 144f, false, paint)
    }

    fun drawPlacement(canvas: Canvas, key: ResolvedKey) = placement.draw(canvas, key.bounds, paint.color)

    /** Draws a T-shirt / theme icon (Laban Key style) for instant theme switching. */
    fun drawTheme(canvas: Canvas, key: ResolvedKey) {
        val w = min(key.bounds.width, key.bounds.height) * 0.42f
        val h = w * 0.9f
        val cx = key.bounds.centerX
        val cy = key.bounds.centerY
        val left = cx - w / 2f
        val right = cx + w / 2f
        val top = cy - h / 2f
        val bottom = cy + h / 2f
        path.reset()
        path.moveTo(cx - w * 0.22f, top)
        path.quadTo(cx, top + h * 0.22f, cx + w * 0.22f, top)
        path.lineTo(right, top + h * 0.16f)
        path.lineTo(right - w * 0.12f, top + h * 0.46f)
        path.lineTo(right - w * 0.24f, top + h * 0.40f)
        path.lineTo(right - w * 0.24f, bottom)
        path.lineTo(left + w * 0.24f, bottom)
        path.lineTo(left + w * 0.24f, top + h * 0.40f)
        path.lineTo(left + w * 0.12f, top + h * 0.46f)
        path.lineTo(left, top + h * 0.16f)
        path.close()
        canvas.drawPath(path, paint)
    }

    /** Draws a gear icon for quick Settings access. */
    fun drawSettings(canvas: Canvas, key: ResolvedKey) {
        val r = min(key.bounds.width, key.bounds.height) * 0.21f
        val cx = key.bounds.centerX
        val cy = key.bounds.centerY
        canvas.drawCircle(cx, cy, r * 0.46f, paint)
        val teeth = 6
        for (i in 0 until teeth) {
            val angle = (i * 60.0) * Math.PI / 180.0
            val cos = kotlin.math.cos(angle).toFloat()
            val sin = kotlin.math.sin(angle).toFloat()
            canvas.drawLine(
                cx + cos * r * 0.72f,
                cy + sin * r * 0.72f,
                cx + cos * r * 1.04f,
                cy + sin * r * 1.04f,
                paint,
            )
        }
        canvas.drawCircle(cx, cy, r * 0.80f, paint)
    }

    /** Draws a chevron (< or >) to expand or collapse the utility toolbar while typing. */
    fun drawToolbarToggle(canvas: Canvas, key: ResolvedKey) {
        val r = min(key.bounds.width, key.bounds.height) * 0.18f
        val cx = key.bounds.centerX
        val cy = key.bounds.centerY
        path.reset()
        if (key.active) {
            // '>' points right to return to predictions
            path.moveTo(cx - r * 0.45f, cy - r)
            path.lineTo(cx + r * 0.55f, cy)
            path.lineTo(cx - r * 0.45f, cy + r)
        } else {
            // '<' points left to reveal full toolbar
            path.moveTo(cx + r * 0.45f, cy - r)
            path.lineTo(cx - r * 0.55f, cy)
            path.lineTo(cx + r * 0.45f, cy + r)
        }
        canvas.drawPath(path, paint)
    }
}
