package com.dicoding.nutridish.signup

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.nutridish.R
import com.dicoding.nutridish.ViewModelFactory
import com.dicoding.nutridish.data.User
import com.dicoding.nutridish.data.Result
import com.dicoding.nutridish.databinding.ActivitySignUpBinding
import com.dicoding.nutridish.login.LoginActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory.getInstance(this)
        signUpViewModel = ViewModelProvider(this, factory)[SignUpViewModel::class.java]

        signUpViewModel.registrationResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                is Result.Error -> {
                    Toast.makeText(this, "Pendaftaran gagal: ${result.error}", Toast.LENGTH_SHORT)
                        .show()
                }
                Result.Loading -> {
                    Toast.makeText(this, "Sedang mendaftar...", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Menambahkan DatePickerDialog ke EditText
        binding.dateEditTextLayout.setOnClickListener {
            showDatePickerDialog()
        }

        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.passwordconfirmEditText.text.toString()
            val dateOfBirthInput = binding.dateEditTextLayout.text.toString()
            val consAlcohol = if (binding.consAlcoholCheckBox.isChecked) 1 else 0
            val consPork = if (binding.consPorkCheckBox.isChecked) 1 else 0
            val weight = binding.weightEditText.text.toString().toFloatOrNull()

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password harus minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(dateOfBirthInput)) {
                Toast.makeText(this, "Tanggal lahir harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dateFormatInput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateFormatOutput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val dateOfBirthFormatted: String
            try {
                val dateOfBirthDate = dateFormatInput.parse(dateOfBirthInput)
                dateOfBirthFormatted = dateFormatOutput.format(dateOfBirthDate!!)
            } catch (e: Exception) {
                Toast.makeText(this, "Format tanggal lahir tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val today = Calendar.getInstance()
            val dobCalendar = Calendar.getInstance().apply {
                time = dateFormatInput.parse(dateOfBirthInput)!!
            }
            var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                age -= 1
            }

            val registrationDate =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())

            val user = User(
                age = age,
                cons_alcohol = consAlcohol,
                cons_pork = consPork,
                dateBirth = dateOfBirthFormatted,
                dateReg = registrationDate,
                email = email,
                userId = 0,
                userName = name,
                weight = weight
            )
            signUpViewModel.registerUser(user)
        }

        // Membuat teks klik untuk login
        val textView = findViewById<TextView>(R.id.messageTextView)
        val text = getString(R.string.message_signup_page)
        val spannableString = SpannableString(text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        val startIndex = text.indexOf("Sign In")
        val endIndex = startIndex + "Sign In".length
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                binding.dateEditTextLayout.setText(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }
}
