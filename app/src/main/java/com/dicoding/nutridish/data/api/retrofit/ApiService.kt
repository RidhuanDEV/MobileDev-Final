package com.dicoding.nutridish.data.api.retrofit

import com.dicoding.nutridish.data.LoginRequest
import com.dicoding.nutridish.data.User
import com.dicoding.nutridish.data.api.response.FileUploadResponse
import com.dicoding.nutridish.data.api.response.RecipeSearchResponseItem
import com.dicoding.nutridish.data.api.response.ResponseRecipeDetail
import com.dicoding.nutridish.data.api.response.UserLoginResponse
import com.dicoding.nutridish.data.api.response.UserRegisterResponse
import okhttp3.MultipartBody
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Path


interface ApiService {

    @POST("auth/register")
    suspend fun registerUser(
        @Body userData: User
    ): Response<UserRegisterResponse>


    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<UserLoginResponse>

    @GET("recipes/search")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("filters") filters: String? = null
    ): Response<List<RecipeSearchResponseItem>>

    @GET("recipe_details/{title}/")
    suspend fun getRecipeDetail(
        @Path("title") title: String
    ): Response<ResponseRecipeDetail>

    @Multipart
    @POST("image/{imageFile}")
    suspend fun uploadImage(
        @Path("imageFile") imageFile: String, // Nama file yang dikirim ke server
        @Part file: MultipartBody.Part  // Bagian file gambar
    ): FileUploadResponse


}