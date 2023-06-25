package com.example.synthtranslator

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.synthtranslator.databinding.FragmentSettingsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsFragmentViewModel
    private lateinit var toast: Toast
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private lateinit var recorder: AudioRecord

    private var lastDBGroup = 6

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        viewModel = ViewModelProvider(this)[SettingsFragmentViewModel::class.java]

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
        toast = Toast.makeText(activity, "Мы не можем записывать голос без разрешения на использование микрофона", Toast.LENGTH_SHORT)

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                changeDBBarrier(p1)
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {}

            override fun onStartTrackingTouch(p0: SeekBar?) {}
        })

        return binding.root
    }

    private fun changeDBBarrier(DB: Int) {
        val newDB = DB + 10

        if (newDB in 100..120 && lastDBGroup != 5) {
            binding.decibelsFactTextview.text = "Отбойный молоток"
            lastDBGroup = 5
        } else if (newDB in 80..99 && lastDBGroup != 4) {
            binding.decibelsFactTextview.text = "Шум московского метро"
            lastDBGroup = 4
        } else if (newDB in 60..79 && lastDBGroup != 3) {
            binding.decibelsFactTextview.text = "Мотоцикл с глушителем"
            lastDBGroup = 3
        } else if (newDB in 40..59 && lastDBGroup != 2) {
            binding.decibelsFactTextview.text = "Пылесос"
            lastDBGroup = 2
        } else if (newDB in 20..39 && lastDBGroup != 1) {
            binding.decibelsFactTextview.text = "Тихий офис"
            lastDBGroup = 1
        } else if (newDB in 10..19 && lastDBGroup != 0) {
            binding.decibelsFactTextview.text = "Шелест листьев"
            lastDBGroup = 0
        }

        val imageResource = when (lastDBGroup) {
            5 -> R.drawable.molotok
            4 -> R.drawable.mosmetro
            3 -> R.drawable.motocycle
            2 -> R.drawable.pilesos
            1 -> R.drawable.office
            else -> {R.drawable.listya}
        }

        binding.decibelsTextview.text = "$newDB Дб"
        binding.decibelsFactImageview.setImageResource(imageResource)
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
        }
    }

    override fun onResume() {
        super.onResume()

        if (view?.let { ContextCompat.checkSelfPermission(it.context, Manifest.permission.RECORD_AUDIO) } != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                toast.show()
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        } else {
            setAudioInstruments()
            viewModel.startRecording(recorder)
        }

        println("onResume")
    }

    override fun onStop() {
        println("onStop")
        viewModel.stopRecording()
        super.onStop()
    }
}