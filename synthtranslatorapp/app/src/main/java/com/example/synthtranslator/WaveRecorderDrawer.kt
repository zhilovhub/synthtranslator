package com.example.synthtranslator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class WaveRecorderDrawer(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint: Paint = Paint()

    init {
        paint.color = Color.rgb(98, 0, 238)
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.drawRect(RectF(20f, 20f, 20f + 30f, 20f + 30f), paint)
    }
}