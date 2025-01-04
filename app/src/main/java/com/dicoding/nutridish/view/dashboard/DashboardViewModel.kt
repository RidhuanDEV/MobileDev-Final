package com.dicoding.nutridish.view.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.nutridish.data.UserRepository
import com.dicoding.nutridish.data.api.response.DailyRecommendationsResponse
import com.dicoding.nutridish.data.api.response.MealPlanResponse
import com.dicoding.nutridish.data.api.response.RecipeSearchResponseItem
import com.dicoding.nutridish.data.database.entity.NutriEntity
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: UserRepository
) : ViewModel(){
    private val _recipesSchedule = MutableLiveData<MealPlanResponse?>(null)
    val recipesSchedule: LiveData<MealPlanResponse?> get() = _recipesSchedule

    private val _dailyRecommendation = MutableLiveData<DailyRecommendationsResponse?>(null)
    val dailyRecommendation: LiveData<DailyRecommendationsResponse?> get() = _dailyRecommendation

    private val _recipesToday = MutableLiveData<List<RecipeSearchResponseItem?>?>(mutableListOf())
    val recipesToday: LiveData<List<RecipeSearchResponseItem?>?> get() = _recipesToday

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    val getFavoriteRecipe: LiveData<List<NutriEntity>> = repository.getBookmarkedNutri()

    fun getScheduleRecipe(id: String) {
        setLoading(true)
        viewModelScope.launch {
            try {
                val result = repository.loadScheduleRecipe(id)
                _recipesSchedule.postValue(result)
            } catch (e: Exception) {
                _recipesSchedule.postValue(null)
            } finally {
                setLoading(false)
            }
        }
    }

    fun getDailyRecommendation(userId: String, date: String) {
        setLoading(true)
        viewModelScope.launch {
            try {
                val result = repository
                    .loadDailyRecommendation(
                    userId = userId,
                    date = date)
                _dailyRecommendation.postValue(result)
            } catch (e: Exception) {
                _dailyRecommendation.postValue(null)
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

}