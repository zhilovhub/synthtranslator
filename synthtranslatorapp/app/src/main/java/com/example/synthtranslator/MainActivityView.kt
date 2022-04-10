package com.example.synthtranslator

import android.media.AudioRecord
import android.media.AudioTrack
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class MainActivityView : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null
    private var synthTranslatorLoop: SynthTranslatorLoop? = null

    fun startLoop() {
        uiScope.launch {
            synthTranslatorLoop?.startLoop()
        }
    }

    fun setAudioInstruments(audioRecord: AudioRecord, audioTrack: AudioTrack) {
        this.audioRecord = audioRecord
        this.audioTrack = audioTrack
        synthTranslatorLoop = SynthTranslatorLoop(audioRecord, audioTrack)
    }

    override fun onCleared() {
        super.onCleared()
        synthTranslatorLoop?.stopLoop()
        uiScope.cancel()
        Log.i("MainActivityView", "OnCleared!!!!!")
    }
}