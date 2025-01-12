package com.dicoding.nutridish.view.favorite

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.nutridish.data.api.response.RecipeSearchResponseItem
import com.dicoding.nutridish.databinding.ItemFavoriteBinding
import com.dicoding.nutridish.view.detail.DetailActivity

class FavoriteAdapter(

) : ListAdapter<RecipeSearchResponseItem, FavoriteAdapter.MyViewHolder>(DIFF_CALLBACK) {

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

    class MyViewHolder(private val binding: ItemFavoriteBinding)
        : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(event: RecipeSearchResponseItem) {
            binding.tvItemName.text = event.title
            binding.textRating.text = event.rating.toString()
            Glide.with(binding.imgItemPhoto.context)
                .load(event.image)
                .into(binding.imgItemPhoto)
            val itemDataList = "recipe_data_list"
            binding.cardView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra(itemDataList, event)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}