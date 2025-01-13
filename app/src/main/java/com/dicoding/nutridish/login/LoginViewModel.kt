package com.dicoding.nutridish.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.nutridish.data.Result
import com.dicoding.nutridish.data.UserRepository
import com.dicoding.nutridish.data.api.response.UserLoginResponse
import com.dicoding.nutridish.data.pref.UserModel
import com.dicoding.nutridish.data.pref.UserPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val userPreference: UserPreference, private val repository: UserRepository): ViewModel() {

    suspend fun saveSession(email :String, password: String, isLogin :Boolean, userId :String) {
        userPreference.saveSession(UserModel(email, password, isLogin, userId))
    }

    private val _loginResult = MutableLiveData<Result<UserLoginResponse>>()
    val loginResult: LiveData<Result<UserLoginResponse>> get() = _loginResult

    fun login(email: String, password: String): LiveData<Result<UserLoginResponse>> {
        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginResult.postValue(result)
        }
        return _loginResult
    }
}