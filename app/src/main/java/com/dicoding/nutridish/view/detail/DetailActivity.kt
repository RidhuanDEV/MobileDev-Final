@file:Suppress("DEPRECATION")

package com.dicoding.nutridish.view.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.nutridish.R
import com.dicoding.nutridish.ViewModelFactory
import com.dicoding.nutridish.data.database.entity.NutriEntity
import com.dicoding.nutridish.databinding.ActivityDetailBinding
import kotlinx.coroutines.launch
import com.dicoding.nutridish.data.Result
import com.dicoding.nutridish.data.api.response.RecipeSearchResponseItem
import org.json.JSONArray
import org.json.JSONException

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var database: NutriEntity
    private lateinit var viewModel: DetailViewModel
    val data = "recipe_data_list"

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        binding.backButton.setOnClickListener {
            finish()
        }

        val nutriItem: RecipeSearchResponseItem? = intent.getParcelableExtra(data)

        if (nutriItem != null) {
            showLoading(true)
            // Set the dish title
            binding.dishTitle.text = nutriItem.title ?: "Default Recipe"

            // Set the image
            Glide.with(binding.root.context)
                .load(nutriItem.image)
                .centerCrop()
                .into(binding.backgroundImage)

            // Set the description
            binding.descriptionText.text = nutriItem.desc ?: "No Description"
            // Set the ingredients list
            val ingredientsJson = nutriItem.ingredients ?: "[]"
            val ingredientsList = try {
                JSONArray(ingredientsJson).let { jsonArray ->
                    (0 until jsonArray.length()).map { jsonArray.getString(it) }
                }
            } catch (e: JSONException) {
                emptyList<String>()
            }


            val formattedIngredients = ingredientsList.joinToString("\n") { it.trim() }

            binding.ingredientsText.text = if (formattedIngredients.isNotEmpty()) {
                "Ingredients:\n$formattedIngredients"
            } else {
                "No Ingredients"
            }

            // Mengonversi Directions (yang berupa String JSON) menjadi Array dan memecahnya berdasarkan titik (.)
            val directionsJson = nutriItem.directions ?: "[]"
            val directionsList = try {
                JSONArray(directionsJson).let { jsonArray ->
                    (0 until jsonArray.length()).map { jsonArray.getString(it) }
                }
            } catch (e: JSONException) {
                emptyList<String>()
            }

            val formattedDirections = directionsList
                .joinToString("\n") { direction ->
                    direction.split(".").joinToString(".\n") { it.trim() }.let {
                        if (it.endsWith(".")) it else "$it."
                    }
                }

            binding.instructionsText.text = if (formattedDirections.isNotEmpty()) {
                "Instructions:\n$formattedDirections"
            } else {
                "No Instructions"
            }


            // Update nutrition card data
            binding.caloriestext.text = nutriItem.calories?.toString() ?: "0"
            binding.proteintext.text = nutriItem.protein?.toString() ?: "0"
            binding.fattext.text = nutriItem.fat?.toString() ?: "0"
            binding.sodiumtext.text = nutriItem.sodium?.toString() ?: "0"
            showLoading(false)
        } else {
            handleError()
        }

        val ivBookmark = binding.favoriteButton
        ivBookmark.setOnClickListener {
            updateBookmarkIcon(database)
        }


        // Initialize NutriEntity
        if (nutriItem != null) {
            database = NutriEntity(
                title = nutriItem.title ?: "Data Is Missing!",
                mediaCover = nutriItem.image ?: "Data Is Missing!",
                calories = nutriItem.calories ?: 0,
                protein = nutriItem.protein ?: 0,
                fat = nutriItem.fat ?: 0,
                sodium = nutriItem.sodium ?: 0,
                ingredients = nutriItem.ingredients ?: "Data Is Missing!",
                directions = nutriItem.directions ?: "Data Is Missing!",
                desc = nutriItem.desc ?: "Data Is Missing!",
                rating = nutriItem.rating ?: 0,
                isBookmarked = true
            )
        }
        viewModel.checkBookmark(database.title.trim()).observe(this) { eventEntity ->
            if (eventEntity != null) {
                binding.favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                binding.favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24)
            }
        }
    }


    private fun updateBookmarkIcon(database: NutriEntity) {
        viewModel.checkBookmark(database.title).observeOnce(this) { isBookmarked ->
            if (isBookmarked != null) {
                viewModel.deleteBookmark(database.title)
                Toast.makeText(
                    this@DetailActivity,
                    "Delete Favorite Recipe Success",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.setBookmark(database)
                Toast.makeText(
                    this@DetailActivity,
                    "Save Favorite Recipe Success",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
        val wrapper = object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        }
        observe(owner, wrapper)
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleError() {
        val message = "Data Is Missing !"
        showLoading(false)
        binding.dishTitle.text = message
    }
}