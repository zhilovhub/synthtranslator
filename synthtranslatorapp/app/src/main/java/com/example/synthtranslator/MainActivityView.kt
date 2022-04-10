package com.example.synthtranslator

import android.media.AudioRecord
import android.media.AudioTrack
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class MainActivityView : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    private var recorder: AudioRecord? = null
    private var player: AudioTrack? = null
    private var synthTranslatorLoop: SynthTranslatorLoop? = null

    fun startRecording() {
        uiScope.launch {
            synthTranslatorLoop?.startLoop()
        }
    }

    fun setAudioInstruments(recorder: AudioRecord, player: AudioTrack) {
        this.recorder = recorder
        this.player = player
        synthTranslatorLoop = SynthTranslatorLoop(recorder, player)
    }

    override fun onCleared() {
        super.onCleared()
        synthTranslatorLoop?.stopLoop()
        uiScope.cancel()
        Log.i("MainActivityView", "OnCleared!!!!!")
    }
}