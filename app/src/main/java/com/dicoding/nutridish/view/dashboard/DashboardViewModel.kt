package com.dicoding.nutridish.view.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.nutridish.data.UserRepository
import com.dicoding.nutridish.data.api.response.MealPlanResponse
import com.dicoding.nutridish.data.api.response.RecipeSearchResponseItem
import com.dicoding.nutridish.data.database.entity.NutriEntity
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: UserRepository
) : ViewModel(){
    private val _recipesRecommended = MutableLiveData<MealPlanResponse?>(null)
    val recipesRecommended: LiveData<MealPlanResponse?> get() = _recipesRecommended

    private val _recipesToday = MutableLiveData<List<RecipeSearchResponseItem?>?>(mutableListOf())
    val recipesToday: LiveData<List<RecipeSearchResponseItem?>?> get() = _recipesToday

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var currentPage = 1
    private var isAllDataLoaded = false
    private var lastQuery: String? = null
    private var lastFilters: String? = null
    private val pageSize = 10
    val getFavoriteRecipe: LiveData<List<NutriEntity>> = repository.getBookmarkedNutri()

    fun getRecommendedRecipe(id: String) {
        setLoading(true)
        viewModelScope.launch {
            try {
                val result = repository.loadRecommendationRecipe(id)
                _recipesRecommended.postValue(result)
            } catch (e: Exception) {
                _recipesRecommended.postValue(null)
            } finally {
                setLoading(false)
            }
        }
    }

    fun getTodayRecipe(query: String, filters: String? = null) {
        // Reset pagination jika query atau filters berubah
        if (query != lastQuery || filters != lastFilters) {
            currentPage = 1
            isAllDataLoaded = false
            _recipesToday.value = mutableListOf()
            lastQuery = query
            lastFilters = filters
        }

        if (isAllDataLoaded) return

        setLoading(true)
        viewModelScope.launch {
            try {
                val result = repository.searchRecipes(query, filters, currentPage, pageSize)
                if (result.isNullOrEmpty()) {
                    isAllDataLoaded = true
                } else {
                    val currentList = _recipesToday.value?.toMutableList() ?: mutableListOf()
                    currentList.addAll(result)
                    _recipesToday.postValue(currentList)
                    currentPage++
                }
            } catch (e: Exception) {
                _recipesToday.postValue(emptyList())
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

}