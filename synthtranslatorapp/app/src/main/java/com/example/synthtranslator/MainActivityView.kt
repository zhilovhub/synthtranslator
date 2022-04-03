package com.example.synthtranslator

import android.util.Log
import androidx.lifecycle.ViewModel

class MainActivityView : ViewModel() {
    init {
        Log.i("MainActivityView", "Model created!")
    }

    fun translateText(text: String) {
        println(SynthTranslatorLoop().execute(text).toString())
    }
}