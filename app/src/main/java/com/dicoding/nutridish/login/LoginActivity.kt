package com.dicoding.nutridish.login

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.nutridish.R
import com.dicoding.nutridish.ViewModelFactory
import com.dicoding.nutridish.data.Result
import com.dicoding.nutridish.databinding.ActivityLoginBinding
import com.dicoding.nutridish.signup.SignUpActivity
import com.dicoding.nutridish.view.HomeActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textView = findViewById<TextView>(R.id.messageTextView)

        val text = getString(R.string.message_login_page)

        val spannableString = SpannableString(text)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                startActivity(intent)
            }
        }

        val startIndex = text.indexOf("account")    
        val endIndex = startIndex + "account".length
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginWithEmail(email, password)
            } else {
                Toast.makeText(this, "Email dan Password harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        lifecycleScope.launch {
            viewModel.login(email, password).observe(this@LoginActivity) { result ->
                when (result) {
                    is Result.Success -> {
                        lifecycleScope.launch {
                            viewModel.saveSession(email, password, true, result.data.userId.toString())
                            Log.d("Login", "Login berhasil: ${result.data.userId}")
                        }
                        Toast.makeText(this@LoginActivity, "Login berhasil!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    is Result.Error -> {
                        Log.d("Login", "Login failed: ${result.error}")
                    }

                    is Result.Loading -> {
                        Toast.makeText(this@LoginActivity, "Sedang login...", Toast.LENGTH_SHORT).show()
                    }

                    else -> {   // Result.Empty
                        Log.d("Login", "Login failed: ${"Error"}")
                    }
                }
            }
        }
    }
}



