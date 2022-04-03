package com.example.synthtranslator

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.synthtranslator.translator.SynthTranslator
import kotlinx.coroutines.*

class MainActivityView : ViewModel() {
    init {
        Log.i("MainActivityView", "Model created!")
    }

    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var result: String = ""

    fun translateText(text: String) {
        uiScope.launch {
            result = suspendTranslateText(text)
        }
    }

    private suspend fun suspendTranslateText(text: String): String {
        return withContext(Dispatchers.IO) {
            val resultValue = SynthTranslator().translate(text)
            resultValue
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}