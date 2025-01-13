package com.dicoding.nutridish.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.nutridish.data.UserRepository
import com.dicoding.nutridish.data.api.response.UserResponse
import com.dicoding.nutridish.data.api.retrofit.ApiService
import com.dicoding.nutridish.data.pref.UserPreference
import kotlinx.coroutines.launch

class ProfileViewModel(private val userPreference: UserPreference, private val userRepository: UserRepository): ViewModel() {

    private val _userResult = MutableLiveData<UserResponse?>()
    val userResult : LiveData<UserResponse?> = _userResult

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun getUser(id : String) : LiveData<UserResponse?> {
        viewModelScope.launch {
            val user = userRepository.getUser(id)
            _userResult.postValue(user)
        }
        return userResult
    }
}