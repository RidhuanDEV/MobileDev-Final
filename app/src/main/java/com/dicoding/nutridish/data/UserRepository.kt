package com.dicoding.nutridish.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.nutridish.data.api.response.DailyRecommendationsResponse
import com.dicoding.nutridish.data.api.response.FileUploadResponse
import com.dicoding.nutridish.data.api.response.MealPlanResponse
import com.dicoding.nutridish.data.api.response.RecipeSearchResponseItem
import com.dicoding.nutridish.data.api.response.ResponseRecipeDetail
import com.dicoding.nutridish.data.api.response.UserLoginResponse
import com.dicoding.nutridish.data.api.response.UserRegisterResponse
import com.dicoding.nutridish.data.api.retrofit.ApiConfig
import com.dicoding.nutridish.data.api.retrofit.ApiService
import com.dicoding.nutridish.data.database.entity.NutriEntity
import com.dicoding.nutridish.data.database.room.NutriDao
import com.dicoding.nutridish.data.pref.UserModel
import com.dicoding.nutridish.data.pref.UserPreference
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val nutriDao: NutriDao
) {
    suspend fun getNutriDetail(title: String): Response<ResponseRecipeDetail> {
        return apiService.getRecipeDetail(title)
    }

    fun getBookmarkedNutri(): LiveData<List<NutriEntity>> {
        return nutriDao.getBookmarkedNutri() // Mengambil data dari Room sebagai LiveData
    }

    suspend fun setBookmark(recipe: NutriEntity) {
        nutriDao.insertNutri(recipe)
    }

    suspend fun deleteBookmark(title: String) {
        nutriDao.deleteNutriById(title)
    }

    fun checkBookmark(id: String): LiveData<NutriEntity?> {
        return nutriDao.getFavoriteNutriById(id)
    }

    suspend fun registerUser(
        age: Int,
        consAlcohol: Int,
        consPork: Int,
        dateBirth: String,
        dateReg: String,
        email: String,
        userName: String,
        password: String,
        weight: Float?
    ): UserRegisterResponse {
        val userData = User(
            age = age,
            consAlcohol = consAlcohol,
            consPork = consPork,
            dateBirth = dateBirth,
            dateReg = dateReg,
            email = email,
            userName = userName,
            password = password,
            weight = weight
        )

        val response = apiService.registerUser(userData)

        if (!response.isSuccessful) {
            throw Exception("Registration failed: ${response.errorBody()?.string()}")
        }

        return response.body() ?: throw Exception("Response body is null")
    }


    suspend fun login(email: String, password: String): Result<UserLoginResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)
            if (response.isSuccessful) {
                val userLoginResponse = response.body() // Get the response body as UserLoginResponse
                Result.Success(userLoginResponse ?: UserLoginResponse()) // Return the response object directly
            } else {
                Result.Error(Exception(response.message()).toString())
            }
        } catch (e: Exception) {
            Result.Error(e.toString())
        }
    }
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }
    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun searchRecipes(query: String, filters: String?, page: Int, pageSize: Int): List<RecipeSearchResponseItem?>? {
        return try {
            val response = apiService.searchRecipes(query, filters, page, pageSize)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun loadScheduleRecipe(id : String): MealPlanResponse {
        return try {
            userPreference.getSession()
            val response = apiService.getScheduleRecipe(id)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Response body is null")
            } else {
                throw Exception("Failed to load recommendation recipe: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            throw Exception("Failed to load recommendation recipe: ${e.message}")
        }
    }

    suspend fun loadRecipes(query: String, filters: String?, page: Int, pageSize: Int): List<RecipeSearchResponseItem?>? {
        return try {
            val response = apiService.searchRecipes(query, filters, page, pageSize)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun loadDailyRecommendation(userId : String, date : String): DailyRecommendationsResponse {
        return try {
            val response = apiService.getDailyRecommendation(UserRecommendation(userId, date))
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Response body is null")
            } else {
                throw Exception("Failed to load daily recommendation: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            throw Exception("Failed to load daily recommendation: ${e.message}")
        }
    }


    fun uploadImage(imageFile: File) = liveData {
        emit(Result.Loading)
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = ApiConfig.getApiService().uploadImage(imageFile.name,multipartBody)
            Log.d("API Response", "Response: $successResponse")
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("API Error", "Error Body: $errorBody")
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            emit(errorResponse.message?.let { Result.Error(it) })
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Unexpected Error Occurred"
            emit(Result.Error(errorMessage))
        }


    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference, dao: NutriDao): UserRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UserRepository(userPreference, apiService, dao)
                INSTANCE = instance
                instance
            }
        }
    }
}
