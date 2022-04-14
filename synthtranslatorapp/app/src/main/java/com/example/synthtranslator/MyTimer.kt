package com.example.synthtranslator

import android.os.Looper
import android.os.Handler

class MyTimer(listener: OnTimerTickListener) {
    interface OnTimerTickListener {
        fun onTimerTick()
    }

    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private var interval: Long = 100L

    init {
        runnable = Runnable {
            handler.postDelayed(runnable, interval)
            listener.onTimerTick()
        }
    }

    fun start() {
        handler.postDelayed(runnable, interval)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
    }
}
