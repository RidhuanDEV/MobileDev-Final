package com.dicoding.nutridish.data.api.response

import com.google.gson.annotations.SerializedName

data class UserRegisterResponse(

	@field:SerializedName("meal_plan")
	val mealPlan: MealPlan? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("userId")
	val userId: Int? = null
)