package com.example.synthtranslator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import com.example.synthtranslator.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    private var lastDBGroup = 6

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)

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

        binding.decibelsTextview.text = "$newDB Дб"
    }
}