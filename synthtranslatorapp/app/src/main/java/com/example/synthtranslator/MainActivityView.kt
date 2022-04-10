package com.example.synthtranslator

import android.media.AudioRecord
import android.media.AudioTrack
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityView : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Default + viewModelJob)
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
        viewModelJob.cancel()
        player?.release()
        recorder?.release()
        Log.i("MainActivityView", "OnCleared!!!!!")
    }
}