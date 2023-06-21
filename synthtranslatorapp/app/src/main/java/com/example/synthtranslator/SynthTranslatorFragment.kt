package com.example.synthtranslator

import android.Manifest
import android.content.pm.PackageManager
import android.media.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.synthtranslator.databinding.SynthtranslatorFragmentBinding
import kotlinx.android.synthetic.main.synthtranslator_fragment.*


class SynthTranslatorFragment : Fragment(), MyTimer.OnTimerTickListener {
    private lateinit var binding: SynthtranslatorFragmentBinding
    private lateinit var viewModel: SynthTranslatorFragmentViewModel
    private lateinit var toast: Toast
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private lateinit var myTimer: MyTimer

    lateinit var recorder: AudioRecord
    lateinit var player: AudioTrack

    private var audioInstrumentsCreated: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.synthtranslator_fragment, container, false)
        viewModel = ViewModelProvider(this)[SynthTranslatorFragmentViewModel::class.java]

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
        toast = Toast.makeText(activity, "Мы не можем записывать голос без разрешения на использование микрофона", Toast.LENGTH_SHORT)
        myTimer = MyTimer(this)

        setListeners()
        setObservers()

        return binding.root
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
        viewModel.recognizedTextLiveData.observe(viewLifecycleOwner) { recognizedText ->
            binding.recognizedText.text = "$recognizedText..."
        }

        viewModel.translatedTextLiveData.observe(viewLifecycleOwner) { translatedText ->
            binding.translatedText.text = "$translatedText..."
        }
    }

    private fun startLoop(view: View) {
        if (ContextCompat.checkSelfPermission(view.context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                toast.show()
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        } else {
            setAudioInstruments()

            myTimer.start()
            waveCanvas.clearAll()
            clearTexts()

            if (!audioInstrumentsCreated) {
                audioInstrumentsCreated = true
                viewModel.startLoop(recorder, player)
            } else {
                viewModel.continueLoop(recorder, player)
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
        if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.RECORD_AUDIO) } == PackageManager.PERMISSION_GRANTED) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        println("OnCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        println("onStart")
        super.onStart()
    }

    override fun onResume() {
        println("onResume")
        super.onResume()
    }

    override fun onPause() {
        println("onPause")
        super.onPause()
    }

    override fun onStop() {
        println("onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        println("onDestroyView")
        super.onDestroyView()
    }
}