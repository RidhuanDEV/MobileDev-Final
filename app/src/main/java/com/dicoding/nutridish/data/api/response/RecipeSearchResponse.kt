package com.dicoding.nutridish.data.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class RecipeSearchResponse(

	@field:SerializedName("RecipeSearchResponse")
	val recipeSearchResponse: List<RecipeSearchResponseItem?>? = null
) : Parcelable

@Parcelize
data class RecipeSearchResponseItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("meal_type")
	val mealType: MealType? = null,

	@field:SerializedName("rating")
	val rating: @RawValue Any? = null,

	@field:SerializedName("calories")
	val calories: @RawValue Any? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("sodium")
	val sodium: @RawValue Any? = null,

	@field:SerializedName("directions")
	val directions: String? = null,

	@field:SerializedName("protein")
	val protein: @RawValue Any? = null,

	@field:SerializedName("fat")
	val fat: @RawValue Any? = null,

	@field:SerializedName("ingredients")
	val ingredients: String? = null,

	@field:SerializedName("categories")
	val categories: String? = null,

	@field:SerializedName("desc")
	val desc: String? = null
) : Parcelable

@Parcelize
data class MealType(

	@field:SerializedName("lunch")
	val lunch: Int? = null,

	@field:SerializedName("dessert")
	val dessert: Int? = null,

	@field:SerializedName("snack")
	val snack: Int? = null,

	@field:SerializedName("breakfast")
	val breakfast: Int? = null,

	@field:SerializedName("dinner")
	val dinner: Int? = null
) : Parcelable
