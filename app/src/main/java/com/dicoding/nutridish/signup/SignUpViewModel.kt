package com.dicoding.nutridish.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.nutridish.data.User
import com.dicoding.nutridish.data.UserRepository
import kotlinx.coroutines.launch
import com.dicoding.nutridish.data.Result

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _registrationResult = MutableLiveData<Result<Unit>>()
    val registrationResult: LiveData<Result<Unit>> get() = _registrationResult

    fun registerUser(user: User) {
        viewModelScope.launch {
            val result = userRepository.registerUser(user)
            _registrationResult.value = result
        }
    }
}
