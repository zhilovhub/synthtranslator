package com.example.synthtranslator

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.media.AudioManager

import com.example.synthtranslator.databinding.ActivityMainBinding

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.databinding.DataBindingUtil
import androidx.core.app.ActivityCompat

import android.os.Bundle
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityView
    private lateinit var toast: Toast
    private var audioInstrumentsCreated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityView::class.java)
        toast = Toast.makeText(this, "Мы не можем записывать голос без разрешения на использование микрофона", Toast.LENGTH_SHORT)

        setListeners()
    }

    private fun setListeners() {
        binding.startButton.setOnClickListener {view: View ->
            startLoop(view)
        }

        binding.stopButton.setOnClickListener {view: View ->
            stopLoop(view)
        }
    }

    private fun startLoop(view: View) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                toast.show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1234)
            }
        } else {
            if (!audioInstrumentsCreated) {
                val minBufferSizeRecording = AudioRecord.getMinBufferSize(
                    16000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val recorder = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    16000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSizeRecording * 4
                )

                val minBufferSizePlaying = AudioTrack.getMinBufferSize(
                    48000,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val player = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    48000,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSizePlaying * 4,
                    AudioTrack.MODE_STREAM
                )

                viewModel.setAudioInstruments(recorder, player)
                audioInstrumentsCreated = true
                viewModel.startLoop()
            } else {
                viewModel.continueLoop()
            }
            view.isEnabled = false
            binding.stopButton.isEnabled = true
        }
    }

    private fun stopLoop(view: View) {
        view.isEnabled = false
        binding.startButton.isEnabled = true
        viewModel.pauseLoop()
    }
}
