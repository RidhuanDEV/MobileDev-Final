package com.dicoding.nutridish.data.api.response

import com.google.gson.annotations.SerializedName

data class MealPlanResponse(

	@field:SerializedName("meal_plan")
	val mealPlan: MealPlan? = null,

	@field:SerializedName("user_id")
	val userId: String? = null
)

data class MealPlan(

	@field:SerializedName("lunch")
	val lunch: List<LunchItem?>? = null,

	@field:SerializedName("dessert")
	val dessert: List<DessertItem?>? = null,

	@field:SerializedName("snack")
	val snack: List<SnackItem?>? = null,

	@field:SerializedName("breakfast")
	val breakfast: List<BreakfastItem?>? = null,

	@field:SerializedName("dinner")
	val dinner: List<DinnerItem?>? = null
)

data class DinnerItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("date_used")
	val dateUsed: String? = null,

	@field:SerializedName("meal_type")
	val mealType: MealType? = null,

	@field:SerializedName("rating")
	val rating: Any? = null,

	@field:SerializedName("calories")
	val calories: Any? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("sodium")
	val sodium: Any? = null,

	@field:SerializedName("directions")
	val directions: String? = null,

	@field:SerializedName("protein")
	val protein: Any? = null,

	@field:SerializedName("fat")
	val fat: Any? = null,

	@field:SerializedName("ingredients")
	val ingredients: String? = null,

	@field:SerializedName("categories")
	val categories: String? = null,

	@field:SerializedName("desc")
	val desc: String? = null
)

data class MealTypes(

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
)

data class SnackItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("date_used")
	val dateUsed: String? = null,

	@field:SerializedName("meal_type")
	val mealType: MealType? = null,

	@field:SerializedName("rating")
	val rating: Any? = null,

	@field:SerializedName("calories")
	val calories: Any? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("sodium")
	val sodium: Any? = null,

	@field:SerializedName("directions")
	val directions: String? = null,

	@field:SerializedName("protein")
	val protein: Any? = null,

	@field:SerializedName("fat")
	val fat: Any? = null,

	@field:SerializedName("ingredients")
	val ingredients: String? = null,

	@field:SerializedName("categories")
	val categories: String? = null,

	@field:SerializedName("desc")
	val desc: String? = null
)

data class LunchItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("date_used")
	val dateUsed: String? = null,

	@field:SerializedName("meal_type")
	val mealType: MealType? = null,

	@field:SerializedName("rating")
	val rating: Any? = null,

	@field:SerializedName("calories")
	val calories: Any? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("sodium")
	val sodium: Any? = null,

	@field:SerializedName("directions")
	val directions: String? = null,

	@field:SerializedName("protein")
	val protein: Any? = null,

	@field:SerializedName("fat")
	val fat: Any? = null,

	@field:SerializedName("ingredients")
	val ingredients: String? = null,

	@field:SerializedName("categories")
	val categories: String? = null,

	@field:SerializedName("desc")
	val desc: String? = null
)

data class DessertItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("date_used")
	val dateUsed: String? = null,

	@field:SerializedName("meal_type")
	val mealType: MealType? = null,

	@field:SerializedName("rating")
	val rating: Any? = null,

	@field:SerializedName("calories")
	val calories: Any? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("sodium")
	val sodium: Any? = null,

	@field:SerializedName("directions")
	val directions: String? = null,

	@field:SerializedName("protein")
	val protein: Any? = null,

	@field:SerializedName("fat")
	val fat: Any? = null,

	@field:SerializedName("ingredients")
	val ingredients: String? = null,

	@field:SerializedName("categories")
	val categories: String? = null,

	@field:SerializedName("desc")
	val desc: String? = null
)

data class BreakfastItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("date_used")
	val dateUsed: String? = null,

	@field:SerializedName("meal_type")
	val mealType: MealType? = null,

	@field:SerializedName("rating")
	val rating: Any? = null,

	@field:SerializedName("calories")
	val calories: Any? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("sodium")
	val sodium: Any? = null,

	@field:SerializedName("directions")
	val directions: String? = null,

	@field:SerializedName("protein")
	val protein: Any? = null,

	@field:SerializedName("fat")
	val fat: Any? = null,

	@field:SerializedName("ingredients")
	val ingredients: String? = null,

	@field:SerializedName("categories")
	val categories: String? = null,

	@field:SerializedName("desc")
	val desc: Any? = null
)
