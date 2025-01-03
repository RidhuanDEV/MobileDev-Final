package com.dicoding.nutridish.view.explore

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.nutridish.R
import com.dicoding.nutridish.data.api.response.RecipeSearchResponseItem
import com.dicoding.nutridish.databinding.ItemRecentlyAddedBinding
import com.dicoding.nutridish.view.detail.DetailActivity

class ExploreAdapter(
    private val onLoading: (Boolean) -> Unit
) : ListAdapter<RecipeSearchResponseItem, ExploreAdapter.RecipeViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RecipeSearchResponseItem>() {
            override fun areItemsTheSame(oldItem: RecipeSearchResponseItem, newItem: RecipeSearchResponseItem): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: RecipeSearchResponseItem, newItem: RecipeSearchResponseItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    fun updateRecipes(newRecipes: List<RecipeSearchResponseItem>) {
        submitList(newRecipes)
    }

    class RecipeViewHolder(private val binding: ItemRecentlyAddedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: RecipeSearchResponseItem, onLoading: (Boolean) -> Unit) {
            onLoading(true)
            binding.textFoodName.text = recipe.title
            binding.textRating.text = recipe.rating.toString()

            Glide.with(binding.root.context)
                .load(recipe.image)
                .centerCrop()
                .into(binding.imageFood)

            binding.cardView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("recipe_data_list", recipe)
                itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
            }
            onLoading(false)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecentlyAddedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position), onLoading)
    }
}
