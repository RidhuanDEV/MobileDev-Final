package com.dicoding.nutridish.data

data class User(
    val age: Int,
    val cons_alcohol: Int,
    val cons_pork: Int,
    val dateBirth: String,
    val dateReg: String,
    val email: String,
    val userId: Int,
    val userName: String,
    val weight: Float?
)
