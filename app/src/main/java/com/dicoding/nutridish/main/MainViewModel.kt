package com.dicoding.nutridish.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.nutridish.data.Result
import com.dicoding.nutridish.data.UserRepository
import com.dicoding.nutridish.data.api.response.LoginResponse
import com.dicoding.nutridish.data.api.response.RegisterResponse
import com.dicoding.nutridish.data.api.response.UserLoginResponse
import com.dicoding.nutridish.data.pref.UserModel
import com.dicoding.nutridish.data.pref.UserPreference
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository, private val userPreference: UserPreference) : ViewModel() {
    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _loginResult = MutableLiveData<Result<UserLoginResponse>>()
    val loginResult: LiveData<Result<UserLoginResponse>> get() = _loginResult


    fun getSession(): LiveData<UserModel> {
        return userPreference.getSession().asLiveData() // Convert Flow to LiveData
    }

    private fun saveSession(user: UserModel) {
        viewModelScope.launch {
            userPreference.saveSession(user)
        }
    }

    fun login(email: String, password: String): LiveData<Result<UserLoginResponse>> {
        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginResult.postValue(result)
        }
        return _loginResult
    }

    suspend fun logout() {
        userPreference.logout()
    }

}