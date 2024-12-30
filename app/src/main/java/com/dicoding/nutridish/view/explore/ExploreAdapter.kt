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

        private val dietaryIconMap = mapOf(
            "vegetarian" to Pair(R.drawable.salad, "Vegetarian"),
            "vegan" to Pair(R.drawable.vegan, "Vegan"),
            "soy free" to Pair(R.drawable.soy_free, "Soy Free"),
            "peanut free" to Pair(R.drawable.peanut_free, "Peanut Free"),
            "wheat free" to Pair(R.drawable.no_wheat, "Wheat Free"),
            "pescatarian" to Pair(R.drawable.fish, "Pescatarian"),
            "dairy free" to Pair(R.drawable.dairy_free, "Dairy Free"),
            "paleo" to Pair(R.drawable.paleo, "Paleo"),
            "low cal" to Pair(R.drawable.low_calorie, "Low Cal"),
            "low cholesterol" to Pair(R.drawable.ldl, "Low Cholesterol"),
            "low fat" to Pair(R.drawable.no_fat, "Low Fat"),
            "low carb" to Pair(R.drawable.ldl, "Low Carb"),
            "low sodium" to Pair(R.drawable.sodium, "Low Sodium"),
            "fat free" to Pair(R.drawable.no_fat_, "Fat Free")
        )

        fun bind(recipe: RecipeSearchResponseItem, onLoading: (Boolean) -> Unit) {
            onLoading(true)
            binding.textFoodName.text = recipe.title

            binding.iconGrid.removeAllViews()

            val horizontalLayout = LinearLayout(binding.root.context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = GridLayout.LayoutParams().apply {
                    width = GridLayout.LayoutParams.WRAP_CONTENT
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    marginEnd = 16
                    bottomMargin = 16
                }
            }

            val imageView = ImageView(binding.root.context).apply {
                val sizeInDp = 20
                val density = resources.displayMetrics.density
                val sizeInPx = (sizeInDp * density).toInt()

                layoutParams = LinearLayout.LayoutParams(sizeInPx, sizeInPx)

                Glide.with(binding.root.context)
                    .load(recipe.image)
                    .into(this)
            }

            val textView = TextView(binding.root.context).apply {
                text = recipe.title
                textSize = 10f
                setPadding(8, 5, 0, 0)
            }

            horizontalLayout.addView(imageView)
            horizontalLayout.addView(textView)

            // Tambahkan layout horizontal ke dalam GridLayout
            binding.iconGrid.addView(horizontalLayout)

            val healthyLayout = LinearLayout(binding.root.context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = GridLayout.LayoutParams().apply {
                    width = GridLayout.LayoutParams.WRAP_CONTENT
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    marginEnd = 16
                    bottomMargin = 16
                }
            }

            val healthyIcon = ImageView(binding.root.context).apply {
                setImageResource(R.drawable.healthy_diet)

                val sizeInDp = 20
                val density = resources.displayMetrics.density
                val sizeInPx = (sizeInDp * density).toInt()

                layoutParams = LinearLayout.LayoutParams(sizeInPx, sizeInPx)
            }

            val healthyText = TextView(binding.root.context).apply {
                text = "Healthy"
                textSize = 10f
                setPadding(8, 5, 0, 0)
            }

            healthyLayout.addView(healthyIcon)
            healthyLayout.addView(healthyText)

            binding.iconGrid.addView(healthyLayout)

            binding.cardView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("recipe_data_list", recipe)
//                context.startActivity(intent)
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
