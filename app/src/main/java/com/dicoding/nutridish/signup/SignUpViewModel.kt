package com.dicoding.nutridish.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.nutridish.data.User
import com.dicoding.nutridish.data.UserRepository
import kotlinx.coroutines.launch
import com.dicoding.nutridish.data.Result
import com.dicoding.nutridish.data.api.response.UserRegisterResponse

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _registrationResult = MutableLiveData<Result<UserRegisterResponse>>()
    val registrationResult: LiveData<Result<UserRegisterResponse>> get() = _registrationResult
    fun registerUser(
        age: Int,
        consAlcohol: Int,
        consPork: Int,
        dateBirth: String,
        dateReg: String,
        email: String,
        userName: String,
        password: String,
        weight: Float?
    ) {
        viewModelScope.launch {
            _registrationResult.value = Result.Loading
            try {
                val response = userRepository.registerUser(
                    age = age,
                    consAlcohol = consAlcohol,
                    consPork = consPork,
                    dateBirth = dateBirth,
                    dateReg = dateReg,
                    email = email,
                    userName = userName,
                    password = password,
                    weight = weight
                )
                _registrationResult.value = Result.Success(response)
            } catch (e: Exception) {
                _registrationResult.value = Result.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }
}
