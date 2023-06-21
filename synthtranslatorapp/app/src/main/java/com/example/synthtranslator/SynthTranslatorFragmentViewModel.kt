package com.example.synthtranslator

import android.media.AudioRecord
import android.media.AudioTrack
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class SynthTranslatorFragmentViewModel : ViewModel() {
    val recognizedTextLiveData = MutableLiveData("")
    val translatedTextLiveData = MutableLiveData("")

    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    private lateinit var synthTranslatorLoop: SynthTranslatorLoop

    private var loopOnceStarted = false

    fun startLoop(audioRecord: AudioRecord, audioTrack: AudioTrack) {
        audioRecord.startRecording()
        audioTrack.play()

        if (!loopOnceStarted) {
            loopOnceStarted = true
        }
        synthTranslatorLoop = SynthTranslatorLoop(recognizedTextLiveData, translatedTextLiveData,
            audioRecord, audioTrack)
        uiScope.launch {
            synthTranslatorLoop.startLoop()
        }
    }

    fun pauseLoop() {
        synthTranslatorLoop.pauseLoop()
    }

    fun continueLoop(audioRecord: AudioRecord, audioTrack: AudioTrack) {
        audioRecord.startRecording()
        audioTrack.play()

        synthTranslatorLoop.continueLoop(audioRecord, audioTrack)
    }

    fun getAmplitude(): Int {
        return synthTranslatorLoop.getAmplitude()
    }

    override fun onCleared() {
        super.onCleared()

        if (loopOnceStarted) {
            synthTranslatorLoop.stopLoop()
            uiScope.cancel()
        }

        Log.i("MainActivityView", "OnCleared!!!!!")
    }
}