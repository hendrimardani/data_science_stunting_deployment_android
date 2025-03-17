package com.example.stunting

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class CustomTextProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var text: String = "CREATED BY" // Default teks
    private var textSize = 110f
    private var progressColor = resources.getColor(R.color.pink) // Default warna progress

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE // Warna teks
        style = Paint.Style.FILL
        typeface = Typeface.DEFAULT_BOLD
    }

    private val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK // Warna outline teks
        style = Paint.Style.STROKE
        strokeWidth = 5f
        typeface = Typeface.DEFAULT_BOLD
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 30f
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val textPath = Path()
    private var progress = 0f // Animasi progress 0-1

    init {
        textPaint.textSize = textSize
        outlinePaint.textSize = textSize
        progressPaint.color = progressColor
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val widthCenter = width / 2f
        val heightCenter = height / 2f

        val bounds = Rect()
        textPaint.getTextBounds(text, 0, text.length, bounds)

        val x = widthCenter - bounds.width() / 2f
        val y = heightCenter + bounds.height() / 2f

        textPath.reset()
        textPaint.getTextPath(text, 0, text.length, x, y, textPath)

        // Gambar outline & teks utama
        canvas.drawPath(textPath, outlinePaint)
        canvas.drawText(text, x, y, textPaint)

        // Simpan canvas dan buat clipping path berbentuk teks
        canvas.save()
        canvas.clipPath(textPath)

        // Path untuk efek progress
        val pathMeasure = PathMeasure(textPath, false)
        val pathEffect = DashPathEffect(
            floatArrayOf(pathMeasure.length * progress, pathMeasure.length * (1 - progress)),
            0f
        )

        progressPaint.pathEffect = pathEffect
        canvas.drawPath(textPath, progressPaint)

        // Restore canvas setelah clip
        canvas.restore()

        // Animasi progress
        if (progress < 1f) {
            progress += 0.02f // Kecepatan progress
            postInvalidateDelayed(30)
        } else {
            progress = 0f
            postInvalidateDelayed(500) // Loop ulang dari awal
        }
    }

    // ✅ Setter untuk mengubah teks
    fun setText(newText: String) {
        text = newText
        invalidate() // Refresh tampilan
    }

    // ✅ Setter untuk mengubah warna progress
    fun setProgressColor(color: Int) {
        progressColor = color
        progressPaint.color = color
        invalidate()
    }

    // ✅ Setter untuk mengubah ukuran teks
    fun setTextSize(size: Float) {
        textSize = size
        textPaint.textSize = size
        outlinePaint.textSize = size
        invalidate()
    }
}
