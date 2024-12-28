package com.dicoding.nutridish.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.nutridish.data.pref.UserModel
import com.dicoding.nutridish.data.pref.UserPreference
import com.dicoding.nutridish.data.Result
import com.dicoding.nutridish.data.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userPreference: UserPreference, private val repository: UserRepository): ViewModel() {

    suspend fun saveSession(email :String, password: String, isLogin :Boolean) {
        userPreference.saveSession(UserModel(email, password, isLogin))
    }

    private val _loginResult = MutableLiveData<Result<Unit>>()
    val loginResult: LiveData<Result<Unit>> get() = _loginResult

    fun login(email: String, password: String): LiveData<Result<Unit>> {
        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginResult.postValue(result)
        }
        return _loginResult
    }
}