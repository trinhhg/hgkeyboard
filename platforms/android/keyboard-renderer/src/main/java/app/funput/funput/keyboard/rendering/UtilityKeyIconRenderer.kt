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
    private var isSwiftKeyStyle = false

    fun updateTheme(theme: KeyboardTheme) {
        paint.color = theme.specialLabelColor
        paint.strokeWidth = metrics.dp(1.45f)
        isSwiftKeyStyle = theme.centerSecondaryLabel
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

    /** Draws a SwiftKey-style palette icon for quick theme switching. */
    fun drawTheme(canvas: Canvas, key: ResolvedKey) {
        val r = min(key.bounds.width, key.bounds.height) * 0.21f
        val cx = key.bounds.centerX
        val cy = key.bounds.centerY
        drawingRect.set(cx - r, cy - r, cx + r, cy + r)
        canvas.drawOval(drawingRect, paint)
        val prevStyle = paint.style
        paint.style = Paint.Style.FILL
        val dotR = r * 0.14f
        canvas.drawCircle(cx - r * 0.36f, cy - r * 0.14f, dotR, paint)
        canvas.drawCircle(cx, cy - r * 0.42f, dotR, paint)
        canvas.drawCircle(cx + r * 0.36f, cy - r * 0.14f, dotR, paint)
        canvas.drawCircle(cx + r * 0.22f, cy + r * 0.34f, dotR * 1.15f, paint)
        paint.style = prevStyle
    }

    /** Draws a crisp 8-tooth SwiftKey outline gear icon for Settings. */
    fun drawSettings(canvas: Canvas, key: ResolvedKey) {
        val r = min(key.bounds.width, key.bounds.height) * 0.21f
        val cx = key.bounds.centerX
        val cy = key.bounds.centerY
        canvas.drawCircle(cx, cy, r * 0.38f, paint)
        canvas.drawCircle(cx, cy, r * 0.74f, paint)
        val teeth = 8
        for (i in 0 until teeth) {
            val angle = (i * 45.0) * Math.PI / 180.0
            val cos = kotlin.math.cos(angle).toFloat()
            val sin = kotlin.math.sin(angle).toFloat()
            canvas.drawLine(
                cx + cos * r * 0.76f,
                cy + sin * r * 0.76f,
                cx + cos * r * 1.02f,
                cy + sin * r * 1.02f,
                paint,
            )
        }
    }

    /** Draws a 3x3 dialpad dot grid for instant Numpad (1234) access. */
    fun drawNumpad(canvas: Canvas, key: ResolvedKey) {
        val span = min(key.bounds.width, key.bounds.height) * 0.15f
        val dotR = metrics.dp(1.35f)
        val prevStyle = paint.style
        paint.style = Paint.Style.FILL
        for (row in -1..1) {
            for (col in -1..1) {
                canvas.drawCircle(
                    key.bounds.centerX + col * span,
                    key.bounds.centerY + row * span,
                    dotR,
                    paint,
                )
            }
        }
        paint.style = prevStyle
    }

    /** Draws a crisp lightning bolt outline icon for Quick Shortcuts (Gõ tắt). */
    fun drawShortcuts(canvas: Canvas, key: ResolvedKey) {
        val h = min(key.bounds.width, key.bounds.height) * 0.42f
        val w = h * 0.62f
        val cx = key.bounds.centerX
        val cy = key.bounds.centerY
        path.reset()
        path.moveTo(cx + w * 0.1f, cy - h * 0.5f)
        path.lineTo(cx - w * 0.48f, cy + h * 0.06f)
        path.lineTo(cx - w * 0.02f, cy + h * 0.06f)
        path.lineTo(cx - w * 0.1f, cy + h * 0.5f)
        path.lineTo(cx + w * 0.48f, cy - h * 0.06f)
        path.lineTo(cx + w * 0.02f, cy - h * 0.06f)
        path.close()
        canvas.drawPath(path, paint)
    }

    /** Draws a Laban-style hamburger (≡) or SwiftKey circle-chevron toggle on the toolbar. */
    fun drawToolbarToggle(canvas: Canvas, key: ResolvedKey) {
        val r = min(key.bounds.width, key.bounds.height) * 0.22f
        val cx = key.bounds.centerX
        val cy = key.bounds.centerY
        if (isSwiftKeyStyle) {
            canvas.drawCircle(cx, cy, r * 1.15f, paint)
            path.reset()
            if (key.active) {
                path.moveTo(cx - r * 0.55f, cy + r * 0.2f)
                path.lineTo(cx, cy - r * 0.35f)
                path.lineTo(cx + r * 0.55f, cy + r * 0.2f)
            } else {
                path.moveTo(cx - r * 0.55f, cy - r * 0.2f)
                path.lineTo(cx, cy + r * 0.35f)
                path.lineTo(cx + r * 0.55f, cy - r * 0.2f)
            }
            canvas.drawPath(path, paint)
        } else {
            val halfW = r * 1.1f
            val dy = r * 0.65f
            canvas.drawLine(cx - halfW, cy - dy, cx + halfW, cy - dy, paint)
            canvas.drawLine(cx - halfW, cy, cx + halfW, cy, paint)
            canvas.drawLine(cx - halfW, cy + dy, cx + halfW, cy + dy, paint)
        }
    }
}
