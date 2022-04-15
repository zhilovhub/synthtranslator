package com.example.synthtranslator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.abs

class WaveRecorderDrawer(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint: Paint = Paint()
    private val amplitudes: MutableList<Float> = ArrayList()
    private val rectangles: MutableList<RectF> = ArrayList()

    private val width: Float = 9f
    private val radius: Float = 6f
    private val d: Float = 6f

    private var sw: Float = 0f
    private var sh: Float = 400f

    private var maxRectangles: Int = 0

    init {
        paint.color = Color.rgb(98, 0, 238)
        sw = resources.displayMetrics.widthPixels.toFloat()

        maxRectangles = (sw / (width + d)).toInt()
    }

    fun addAmplitude(amplitude: Float) {
        amplitudes.add(Math.min(abs(amplitude.toInt()) / 7, 400).toFloat())

        rectangles.clear()
        val amps = amplitudes.takeLast(maxRectangles)
        for (i in amps.indices) {
            val left = sw - i * (width + d)
            val top = sh / 2 - amps[i] / 2
            val right = left + width
            val bottom = top + amps[i]
            rectangles.add(RectF(left, top, right, bottom))
        }

        invalidate()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        rectangles.forEach {
            canvas?.drawRoundRect(it, radius, radius, paint)
        }
    }
}