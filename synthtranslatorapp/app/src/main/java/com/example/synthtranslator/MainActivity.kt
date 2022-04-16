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
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MyTimer.OnTimerTickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityView
    private lateinit var toast: Toast

    private lateinit var myTimer: MyTimer

    lateinit var recorder: AudioRecord
    lateinit var player: AudioTrack

    private var audioInstrumentsCreated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityView::class.java)
        toast = Toast.makeText(this, "Мы не можем записывать голос без разрешения на использование микрофона", Toast.LENGTH_SHORT)
        myTimer = MyTimer(this)

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        binding.startButton.setOnClickListener {view: View ->
            startLoop(view)
        }

        binding.stopButton.setOnClickListener {view: View ->
            stopLoop(view)
        }
    }

    private fun setObservers() {
        viewModel.recognizedTextLiveData.observe(this) { recognizedText ->
            binding.recognizedText.text = "$recognizedText..."
        }

        viewModel.translatedTextLiveData.observe(this) { translatedText ->
            binding.translatedText.text = "$translatedText..."
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
            setAudioInstruments()

            viewModel.setAudioInstruments(recorder, player)
            recorder.startRecording()
            player.play()
            myTimer.start()

            waveCanvas.clearAll()
            clearTexts()

            if (!audioInstrumentsCreated) {
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

        myTimer.stop()
        viewModel.pauseLoop()
    }

    private fun setAudioInstruments() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            val minBufferSizeRecording = AudioRecord.getMinBufferSize(
                16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            recorder = AudioRecord(
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
            player = AudioTrack(
                AudioManager.STREAM_MUSIC,
                48000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSizePlaying * 4,
                AudioTrack.MODE_STREAM
            )
        }
    }

    private fun clearTexts() {
        binding.recognizedText.text = "..."
        binding.translatedText.text = "..."
    }

    override fun onTimerTick() {
        waveCanvas.addAmplitude(viewModel.getAmplitude() + 0F)
    }
}
