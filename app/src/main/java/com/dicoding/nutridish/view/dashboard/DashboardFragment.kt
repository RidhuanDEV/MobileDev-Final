package com.dicoding.nutridish.view.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.nutridish.R
import com.dicoding.nutridish.ViewModelFactory
import com.dicoding.nutridish.databinding.FragmentDashboardBinding
import com.dicoding.nutridish.notification.NotificationHelper
import com.dicoding.nutridish.notification.NotificationReceiver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*
import android.os.Build.VERSION_CODES
import androidx.datastore.dataStore
import com.bumptech.glide.Glide
import com.dicoding.nutridish.data.api.response.BreakfastItem
import com.dicoding.nutridish.data.api.response.DinnerItem
import com.dicoding.nutridish.data.api.response.LunchItem
import com.dicoding.nutridish.data.api.response.MealPlan
import com.dicoding.nutridish.data.api.response.MealPlanResponse
import com.dicoding.nutridish.data.api.response.RecipeSearchResponseItem
import com.dicoding.nutridish.data.pref.UserPreference
import com.dicoding.nutridish.data.pref.dataStore
import com.dicoding.nutridish.notification.NotificationsBottomSheet
import com.dicoding.nutridish.view.detail.DetailActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DashboardFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userPreference: UserPreference

    private lateinit var recentlyAdapter: DashboardAdapter
    private lateinit var recommendationAdapter: DashboardRecommendationAdapter

    private var _binding: FragmentDashboardBinding? = null
    private val bindingSafe get() = _binding
    private var job: Job? = null

    private var _isLoading = MutableStateFlow(false)
    private val isLoading = _isLoading.asStateFlow()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)
        return _binding?.root
    }

    private fun showLoading() {
        _isLoading.value = true
        activity?.runOnUiThread {
            bindingSafe?.loadingOverlay?.apply {
                visibility = View.VISIBLE
                bringToFront()
            }
            bindingSafe?.parentLayout?.alpha = 0.5f
        }
    }

    private fun hideLoading() {
        _isLoading.value = false
        activity?.runOnUiThread {
            bindingSafe?.loadingOverlay?.visibility = View.GONE
            bindingSafe?.parentLayout?.alpha = 1.0f
        }
    }

    @RequiresApi(VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreference = UserPreference.getInstance(requireContext().dataStore)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        lifecycleScope.launch {
            isLoading.collect { loading ->
                Log.d("Loading", "Loading state changed: $loading")
            }
        }

        job = lifecycleScope.launch {
            showLoading()
            delay(1000)// Show loading at start
            try {
                setupGreeting()
                setupRecyclerView()
                setupTimeUpdates()
                checkLocationPermissionAndFetchTemperature()
                getScheduleRecipes()
                getRecommendedRecipe()
                scheduleMealNotifications()
            } finally {
                hideLoading() // Hide loading when done
            }
        }
    }

    private suspend fun getUserId(): String? {
        return userPreference.getUserId()
    }

    @RequiresApi(VERSION_CODES.O)
    private fun getScheduleRecipes() {
        val factory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: DashboardViewModel by viewModels { factory }

        lifecycleScope.launch {
            showLoading() // Tampilkan loading di awal
            try {
                val userId = getUserId() ?: throw Exception("User ID tidak ditemukan")

                viewModel.getScheduleRecipe(userId)
                viewModel.recipesSchedule.observe(viewLifecycleOwner) { recipes ->
                    if (recipes == null) {
                        Log.d("DashboardFragment", "Recommended recipes is empty")
                    } else {
                        val currentTime = LocalTime.now()
                        Log.d("DashboardFragment", "Current LocalTime: $currentTime")
                        Log.d("DashboardFragment", "Current Hour: ${currentTime.hour}")

                        when (currentTime.hour) {
                            in 6..12 -> recipes.mealPlan?.breakfast?.map { item ->
                                bindingSafe?.textScheduleTitle?.text = item?.title
                                Glide.with(requireContext())
                                    .load(item?.image)
                                    .into(bindingSafe?.scheduleIcon ?: return@map)
                                bindingSafe?.scheduleWrapper?.setOnClickListener{
                                    val intent = Intent(requireContext(), DetailActivity::class.java)
                                    if (item != null) {
                                        intent.putExtra("recipe_data_list", RecipeSearchResponseItem(
                                            title = item.title ?: "No Title",
                                            image = item.image,
                                            calories = item.calories ?: 0.0,
                                            protein = item.protein ?: 0.0,
                                            sodium = item.sodium ?: 0.0,
                                            fat = item.fat  ?: 0.0,
                                            directions = item.directions ?: "No Instructions",
                                            rating = item.rating ?: 0.0,
                                            desc = item.desc?.toString() ?: "No Description",
                                            ingredients = item.ingredients
                                        ))
                                    }
                                    startActivity(intent)
                                }
                            }
                            in 13..16 -> recipes.mealPlan?.lunch?.map { item ->
                                bindingSafe?.textScheduleTitle?.text = item?.title
                                Glide.with(requireContext())
                                    .load(item?.image)
                                    .into(bindingSafe?.scheduleIcon ?: return@map)
                                bindingSafe?.scheduleWrapper?.setOnClickListener{
                                    val intent = Intent(requireContext(), DetailActivity::class.java)
                                    if (item != null) {
                                        intent.putExtra("recipe_data_list", RecipeSearchResponseItem(
                                            title = item.title ?: "No Title",
                                            image = item.image,
                                            calories = item.calories ?: 0.0,
                                            protein = item.protein ?: 0.0,
                                            sodium = item.sodium ?: 0.0,
                                            fat = item.fat ?: 0.0,
                                            directions = item.directions ?: "No Instructions",
                                            rating = item.rating ?: 0.0,
                                            desc = item.desc?.toString() ?: "No Description",
                                            ingredients = item.ingredients
                                        ))
                                    }
                                    startActivity(intent)
                                }
                            }
                            else -> recipes.mealPlan?.dinner?.map { item ->
                                bindingSafe?.textScheduleTitle?.text = item?.title
                                Glide.with(requireContext())
                                    .load(item?.image)
                                    .into(bindingSafe?.scheduleIcon ?: return@map)
                                bindingSafe?.scheduleWrapper?.setOnClickListener{
                                    val intent = Intent(requireContext(), DetailActivity::class.java)
                                    if (item != null) {
                                        intent.putExtra("recipe_data_list", RecipeSearchResponseItem(
                                            title = item.title ?: "No Title",
                                            image = item.image,
                                            calories = item.calories ?: 0.0,
                                            protein = item.protein ?: 0.0,
                                            sodium = item.sodium ?: 0.0,
                                            fat = item.fat ?: 0.0,
                                            directions = item.directions ?: "No Instructions",
                                            rating = item.rating ?: 0.0,
                                            desc = item.desc?.toString() ?: "No Description",
                                            ingredients = item.ingredients
                                        ))
                                    }
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                    hideLoading() // Sembunyikan loading setelah data selesai diproses
                }
            } catch (e: Exception) {
                hideLoading() // Pastikan loading disembunyikan jika terjadi error
                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("DashboardFragment", "Error loading schedule recipes", e)
            }
        }
    }

    @RequiresApi(VERSION_CODES.O)
    private fun getRecommendedRecipe() {
        val factory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: DashboardViewModel by viewModels { factory }
        recommendationAdapter = DashboardRecommendationAdapter()

        lifecycleScope.launch {
            showLoading() // Tampilkan loading di awal
            try {
                val userId = getUserId() ?: throw Exception("User ID tidak ditemukan")

                val currentTime = LocalTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val formattedTime = currentTime.format(formatter)

                viewModel.getDailyRecommendation(userId, formattedTime)
                viewModel.dailyRecommendation.observe(viewLifecycleOwner) { dailyRecommendation ->
                    if (dailyRecommendation == null) {
                        Log.d("DashboardFragment", "Recommended recipes is empty")
                    } else {
                        val items = dailyRecommendation.recommendations?.map { item ->
                            RecipeSearchResponseItem(
                                title = item?.title ?: "No Title",
                                image = item?.image ?: "",
                                calories = item?.nutrition?.calories ?: 0.0,
                                protein = item?.nutrition?.protein ?: 0.0,
                                sodium = item?.nutrition?.sodium ?: 0.0,
                                fat = item?.nutrition?.fat ?: 0.0,
                                directions = item?.directions ?: "No Instructions",
                                rating = item?.rating ?: 0.0,
                                desc = item?.description ?: "No Description",
                                ingredients = item?.ingredients ?: "No Ingredients"
                            )
                        }
                        recommendationAdapter.submitList(items)

                        bindingSafe?.recyclerViewRecommendation?.apply {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            adapter = recommendationAdapter
                        }
                    }
                    hideLoading() // Sembunyikan loading setelah data selesai diproses
                }
            } catch (e: Exception) {
                hideLoading() // Pastikan loading disembunyikan jika terjadi error
                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("DashboardFragment", "Error loading recommendations", e)
            }
        }
    }

    private fun setupGreeting() {
        bindingSafe?.textGreeting?.text = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning"
            in 12..15 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    private fun setupRecyclerView() {
        val factory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: DashboardViewModel by viewModels { factory }

        recentlyAdapter = DashboardAdapter()

        viewModel.getFavoriteRecipe.observe(viewLifecycleOwner) { recentlyList ->
            if (recentlyList.isNullOrEmpty()) {
                Log.d("DashboardFragment", "Recently list is empty")
            } else {
                val items = recentlyList.map {
                    RecipeSearchResponseItem(
                        title = it.title,
                        image = it.mediaCover,
                        calories = it.calories,
                        protein = it.protein,
                        sodium = it.sodium,
                        fat = it.fat,
                        directions = it.directions,
                        rating = it.rating,
                        desc = it.desc.toString(),
                        ingredients = it.ingredients
                    )
                }
                recentlyAdapter.submitList(items)
            }
        }

        bindingSafe?.recyclerViewRecentlyAdded?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recentlyAdapter
        }

        bindingSafe?.iconNotification?.setOnClickListener {
            NotificationsBottomSheet().show(parentFragmentManager, "NotificationsBottomSheet")
        }

    }

    private fun setupTimeUpdates() {
        lifecycleScope.launch {
            while (isAdded) {
                val currentTime = Calendar.getInstance().time
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                bindingSafe?.textTime?.text = timeFormat.format(currentTime)
                delay(1000)
            }
        }
    }

    private fun checkLocationPermissionAndFetchTemperature() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentTemperature()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentTemperature() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                fetchTemperature(location.latitude, location.longitude)
            } else {
                bindingSafe?.textTemperature?.text = "N/A"
            }
        }
    }

    private fun fetchTemperature(lat: Double, lon: Double) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherApi = retrofit.create(WeatherApi::class.java)
        weatherApi.getCurrentWeather(lat, lon, API_KEY, "metric").enqueue(object :
            retrofit2.Callback<WeatherResponse> {
            override fun onResponse(
                call: retrofit2.Call<WeatherResponse>,
                response: retrofit2.Response<WeatherResponse>
            ) {
                val temp = response.body()?.main?.temp ?: 0.0
                bindingSafe?.textTemperature?.text = "${temp.toInt()}°C"
            }

            override fun onFailure(call: retrofit2.Call<WeatherResponse>, t: Throwable) {
                bindingSafe?.textTemperature?.text = "N/A"
            }
        })
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestNotificationPermission() {
        if (VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    interface WeatherApi {
        @GET("weather")
        fun getCurrentWeather(
            @Query("lat") lat: Double,
            @Query("lon") lon: Double,
            @Query("appid") apiKey: String,
            @Query("units") units: String
        ): retrofit2.Call<WeatherResponse>
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 2
        private const val API_KEY = "ff948dc9af3f8c4ab8c01619e3ac0eb1"
    }

    data class WeatherResponse(
        val main: Main
    )

    data class Main(
        val temp: Double
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        job?.cancel()
    }

    @Deprecated("Deprecated in Java")
    @RequiresApi(VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    scheduleMealNotifications()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Notifikasi dinonaktifkan. Anda akan melewatkan pengingat makanan.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    @RequiresApi(VERSION_CODES.O)
    private fun scheduleMealNotifications() {
        if (!isNotificationPermissionGranted()) {
            requestNotificationPermission()
            return
        }

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
            Toast.makeText(
                requireContext(),
                "Silakan izinkan aplikasi untuk menjadwalkan alarm yang tepat.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val mealTimes = listOf(
            Pair(7, "Breakfast time! Don't forget to eat."),
            Pair(13, "Lunch time! It's important to stay energized."),
            Pair(19, "Dinner time! End your day with a good meal.")
        )

        for ((hour, message) in mealTimes) {
            try {
                val intent =
                    NotificationReceiver.createIntent(requireContext(), "Meal Reminder", message)
                val pendingIntent = PendingIntent.getBroadcast(
                    requireContext(),
                    hour,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    if (before(Calendar.getInstance())) {
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                }

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } catch (e: Exception) {
                Log.e("NotificationError", "Failed to schedule notification: ${e.message}")
            }
        }
    }
}