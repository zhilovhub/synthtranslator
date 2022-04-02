package com.example.synthtranslator

import com.example.synthtranslator.translator.SynthTranslator

import androidx.databinding.DataBindingUtil
import com.example.synthtranslator.databinding.ActivityMainBinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val st = SynthTranslator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setListeners()
    }

    private fun setListeners() {
        binding.startButton.setOnClickListener {
            translateText("Привет, у меня есть проблема! Как дайти до метро")
        }
    }

    private fun translateText(text: String) {
        println(st.execute(text))
    }
}
