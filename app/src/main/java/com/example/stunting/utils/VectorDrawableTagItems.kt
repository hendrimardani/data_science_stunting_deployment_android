package com.example.stunting.utils

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextPaint
import com.magicgoop.tagsphere.item.TagItem
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import android.graphics.Paint.Align.CENTER

class VectorDrawableTagItems( val drawable: Drawable, val resId: Int, val label: String) : TagItem() {

    init {
        drawable.bounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    }

    override fun drawSelf(
        x: Float,
        y: Float,
        canvas: Canvas,
        paint: TextPaint,
        easingFunction: ((t: Float) -> Float)?
    ) {
        canvas.save()
        canvas.translate(x - drawable.intrinsicWidth / 2f, y - drawable.intrinsicHeight / 2f)
        val alpha = easingFunction?.let { calc ->
            val ease = calc(getEasingValue())
            if (!ease.isNaN()) max(0, min(255, (255 * ease).roundToInt())) else 0
        } ?: 255

        val iconWidth = drawable.intrinsicWidth
        val iconHeight = drawable.intrinsicHeight

        // Gambar ikon
        drawable.alpha = alpha
        drawable.draw(canvas)
        canvas.restore()

        // Gambar teks
        paint.alpha = alpha
        paint.textAlign = CENTER
        paint.textSize = 80f // Sesuaikan ukuran teks
        val textY = y + iconHeight - 100f // Jarak di bawah ikon
        canvas.drawText(label, x, textY, paint)
    }
}