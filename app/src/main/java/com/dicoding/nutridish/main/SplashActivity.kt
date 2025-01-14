package com.dicoding.nutridish.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.dicoding.nutridish.ViewModelFactory
import com.dicoding.nutridish.data.Result
import com.dicoding.nutridish.databinding.ActivitySplashBinding
import com.dicoding.nutridish.view.HomeActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setupView()


        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                viewModel.login(user.email, user.password).observe(this) { result ->
                    when (result) {
                        is Result.Success -> {
                            val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        is Result.Error -> {
                            handleLoginFailure()
                        }

                        is Result.Loading -> {
                            Toast.makeText(this@SplashActivity, "Sedang login...", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                lifecycleScope.launch {
                    delay(2000L)
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun handleLoginFailure() {
        lifecycleScope.launch {
            showToast("Authentication failed.")
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}