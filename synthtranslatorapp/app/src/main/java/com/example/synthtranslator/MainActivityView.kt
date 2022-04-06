package com.example.synthtranslator

import androidx.lifecycle.ViewModel
import android.media.AudioRecord

import com.example.synthtranslator.translator.SynthTranslator
import kotlinx.coroutines.*

class MainActivityView : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var result: String = ""
    private lateinit var recorder: AudioRecord
    private lateinit var synthTranslatorLoop: SynthTranslatorLoop

    fun translateText(text: String) {
        uiScope.launch {
            result = suspendTranslateText(text)
        }
    }

    fun setAudioRecorder(recorder: AudioRecord) {
        this.recorder = recorder
        synthTranslatorLoop = SynthTranslatorLoop(recorder)
    }

    fun startRecording() {
        println("Записываем")
        synthTranslatorLoop.startLoop()
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