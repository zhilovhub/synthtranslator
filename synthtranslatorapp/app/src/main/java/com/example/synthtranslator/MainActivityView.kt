package com.example.synthtranslator

import android.media.AudioRecord
import android.media.AudioTrack
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class MainActivityView : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    private var synthTranslatorLoop: SynthTranslatorLoop = SynthTranslatorLoop()

    fun startLoop() {
        uiScope.launch {
            synthTranslatorLoop.startLoop()
        }
    }

    fun pauseLoop() {
        synthTranslatorLoop.pauseLoop()
    }

    fun continueLoop() {
        synthTranslatorLoop.continueLoop()
    }

    fun setAudioInstruments(audioRecord: AudioRecord, audioTrack: AudioTrack) {
        synthTranslatorLoop.setAudioInstruments(audioRecord, audioTrack)
    }

    override fun onCleared() {
        super.onCleared()
        synthTranslatorLoop?.stopLoop()
        uiScope.cancel()
        Log.i("MainActivityView", "OnCleared!!!!!")
    }
}