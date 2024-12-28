package com.dicoding.nutridish.data

data class User(
    val age: Int,
    val consAlcohol: Int,
    val consPork: Int,
    val dateBirth: String,
    val dateReg: String,
    val email: String,
    val userName: String,
    val password: String,
    val weight: Float?
)
