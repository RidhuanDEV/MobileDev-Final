package com.dicoding.nutridish.view.profile.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.nutridish.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener {
            finish()
        }
    }
}