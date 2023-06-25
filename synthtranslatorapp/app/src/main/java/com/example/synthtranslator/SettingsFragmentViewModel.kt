package com.example.synthtranslator

import android.media.AudioRecord
import androidx.lifecycle.ViewModel
import com.example.synthtranslator.recorder.VoiceRecorder
import kotlinx.coroutines.*

class SettingsFragmentViewModel : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    private lateinit var voiceRecorder: VoiceRecorder

    private var onceStarted = false

    fun startRecording(audioRecord: AudioRecord) {
        audioRecord.startRecording()
        voiceRecorder = VoiceRecorder(audioRecord)

        if (!onceStarted) {
            onceStarted = true
        }

        uiScope.launch {
            voiceRecorder.startRecording()
        }
    }

    fun stopRecording() {
        if (onceStarted) {
            try {
                voiceRecorder.stopRecording()
            } catch (e: IllegalStateException) { }
            uiScope.cancel()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopRecording()
    }
}