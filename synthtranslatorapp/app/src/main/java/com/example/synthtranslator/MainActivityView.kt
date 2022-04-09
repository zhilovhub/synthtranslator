package com.example.synthtranslator

import android.media.AudioRecord
import android.media.AudioTrack
import androidx.lifecycle.ViewModel
import com.example.synthtranslator.translator.SynthTranslator
import kotlinx.coroutines.*
import java.io.IOException

class MainActivityView : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Default + viewModelJob)
    private lateinit var recorder: AudioRecord
    private lateinit var player: AudioTrack
    private lateinit var synthTranslatorLoop: SynthTranslatorLoop

    fun startRecording() {
//        uiScope.launch {
//            synthTranslatorLoop.startLoop()
//        }
        synthTranslatorLoop.start()
    }

    fun setAudioRecorder(recorder: AudioRecord, player: AudioTrack) {
        this.recorder = recorder
        this.player = player
        synthTranslatorLoop = SynthTranslatorLoop(recorder, player)
    }

//    private suspend fun suspendStartRecording() {
//        return withContext(Dispatchers.Default) {
//            synthTranslatorLoop.startLoop()
//        }
//    }

//    private suspend fun suspendStartRecording() {
//        return withContext(Dispatchers.IO) {
//            val temp_buffer = ByteArray(1024)
//            var cnt: Int
//
//            var input_stream = SynthTranslator().synthesize("Hello I am testing my program. I should work for a long time")
//
//            try {
//                while (true) {
//                    if (input_stream != null) {
//                        cnt = input_stream.read(temp_buffer, 0, temp_buffer.size)
//                        if (cnt != -1) {
//                            player.write(temp_buffer, 0, cnt)
//                            println(1)
//                        }
//                    }
//                }
//            } catch (e: IOException) {
//                println("Error: $e")
//            }
//        }
//    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}