package com.example.synthtranslator

import androidx.databinding.DataBindingUtil
import com.example.synthtranslator.databinding.ActivityMainBinding

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityView::class.java)

        setListeners()
    }

    private fun setListeners() {
        binding.startButton.setOnClickListener {
            viewModel.translateText("Привет, у меня есть проблема! Как дайти до метро")
        }
    }
}
