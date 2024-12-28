package com.dicoding.nutridish.data.api.response

import com.google.gson.annotations.SerializedName

data class UserLoginResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null
)
