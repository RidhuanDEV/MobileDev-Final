package com.dicoding.nutridish.view.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.nutridish.data.UserRepository
import com.dicoding.nutridish.data.database.entity.NutriEntity

class FavoriteViewModel(private val repository: UserRepository) : ViewModel() {
    val favoriteNutri: LiveData<List<NutriEntity>> = repository.getBookmarkedNutri()

    val favoriteNutris: LiveData<List<NutriEntity>> = repository.getAllFavorites()

    fun searchFavoritesByTitle(query: String): LiveData<List<NutriEntity>> {
        return repository.searchByTitle(query)
    }
}