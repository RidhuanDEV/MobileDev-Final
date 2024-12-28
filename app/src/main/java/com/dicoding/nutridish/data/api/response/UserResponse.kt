package com.dicoding.nutridish.data.api.response

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("dateReg")
	val dateReg: String? = null,

	@field:SerializedName("loc")
	val loc: Any? = null,

	@field:SerializedName("temp")
	val temp: Any? = null,

	@field:SerializedName("cons_pork")
	val consPork: Int? = null,

	@field:SerializedName("cons_alcohol")
	val consAlcohol: Int? = null,

	@field:SerializedName("weight")
	val weight: Any? = null,

	@field:SerializedName("dateBirth")
	val dateBirth: String? = null,

	@field:SerializedName("userName")
	val userName: String? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("age")
	val age: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)
