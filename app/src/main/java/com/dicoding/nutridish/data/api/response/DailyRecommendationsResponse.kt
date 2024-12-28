package com.dicoding.nutridish.data.api.response

import com.google.gson.annotations.SerializedName

data class DailyRecommendationsResponse(

	@field:SerializedName("meal_type")
	val mealType: String? = null,

	@field:SerializedName("provided_time")
	val providedTime: String? = null,

	@field:SerializedName("recommendations")
	val recommendations: List<RecommendationsItem?>? = null
)

data class Dietary(

	@field:SerializedName("wheat_free")
	val wheatFree: Int? = null,

	@field:SerializedName("peanut_free")
	val peanutFree: Int? = null,

	@field:SerializedName("pescatarian")
	val pescatarian: Int? = null,

	@field:SerializedName("dairy_free")
	val dairyFree: Int? = null,

	@field:SerializedName("soy_free")
	val soyFree: Int? = null,

	@field:SerializedName("vegetarian")
	val vegetarian: Int? = null,

	@field:SerializedName("vegan")
	val vegan: Int? = null,

	@field:SerializedName("low_cholesterol")
	val lowCholesterol: Int? = null,

	@field:SerializedName("low_fat")
	val lowFat: Int? = null
)

data class Nutrition(

	@field:SerializedName("sodium")
	val sodium: Any? = null,

	@field:SerializedName("protein")
	val protein: Any? = null,

	@field:SerializedName("fat")
	val fat: Any? = null,

	@field:SerializedName("calories")
	val calories: Any? = null
)

data class RecommendationsItem(

	@field:SerializedName("date_added")
	val dateAdded: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("nutrition")
	val nutrition: Nutrition? = null,

	@field:SerializedName("directions")
	val directions: String? = null,

	@field:SerializedName("dietary")
	val dietary: Dietary? = null,

	@field:SerializedName("meal_type")
	val mealType: String? = null,

	@field:SerializedName("rating")
	val rating: Any? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("ingredients")
	val ingredients: String? = null,

	@field:SerializedName("categories")
	val categories: String? = null,

	@field:SerializedName("title")
	val title: String? = null
)
