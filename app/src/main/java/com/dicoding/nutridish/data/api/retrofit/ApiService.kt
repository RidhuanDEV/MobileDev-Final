package com.dicoding.nutridish.data.api.retrofit

import com.dicoding.nutridish.data.LoginRequest
import com.dicoding.nutridish.data.User
import com.dicoding.nutridish.data.UserRecommendation
import com.dicoding.nutridish.data.api.response.DailyRecommendationsResponse
import com.dicoding.nutridish.data.api.response.FileUploadResponse
import com.dicoding.nutridish.data.api.response.MealPlanResponse
import com.dicoding.nutridish.data.api.response.RecipeSearchResponseItem
import com.dicoding.nutridish.data.api.response.ResponseRecipeDetail
import com.dicoding.nutridish.data.api.response.UserLoginResponse
import com.dicoding.nutridish.data.api.response.UserRegisterResponse
import com.dicoding.nutridish.data.api.response.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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
        @Query("filters") filters: String? = null,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int
    ): Response<List<RecipeSearchResponseItem>>

    @GET("recipe_details/{title}/")
    suspend fun getRecipeDetail(
        @Path("title") title: String
    ): Response<ResponseRecipeDetail>

    @POST("daily-recommendations/")
    suspend fun getDailyRecommendation(
        @Body userRecommendation: UserRecommendation
    ): Response<DailyRecommendationsResponse>

    @GET("meal_plan/{id}/")
    suspend fun getScheduleRecipe(
        @Path("id") id: String
    ): Response<MealPlanResponse>

    @Multipart
    @POST("image/{imageFile}")
    suspend fun uploadImage(
        @Path("imageFile") imageFile: String, // Nama file yang dikirim ke server
        @Part file: MultipartBody.Part  // Bagian file gambar
    ): FileUploadResponse

    @GET("user/{id}")
    suspend fun getUser(
        @Path("id") id: String
    ): Response<UserResponse>


}