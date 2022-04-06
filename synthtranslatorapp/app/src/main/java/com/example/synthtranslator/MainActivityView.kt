package com.example.synthtranslator

import androidx.lifecycle.ViewModel
import android.media.AudioRecord

import kotlinx.coroutines.*

class MainActivityView : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private lateinit var recorder: AudioRecord
    private lateinit var synthTranslatorLoop: SynthTranslatorLoop

    fun startRecording() {
        uiScope.launch {
            suspendStartRecording()
        }
    }

    fun setAudioRecorder(recorder: AudioRecord) {
        this.recorder = recorder
        synthTranslatorLoop = SynthTranslatorLoop(recorder)
    }

    private suspend fun suspendStartRecording() {
        return withContext(Dispatchers.IO) {
            synthTranslatorLoop.startLoop()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}