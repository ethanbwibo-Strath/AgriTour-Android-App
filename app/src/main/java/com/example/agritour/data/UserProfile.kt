package com.example.agritour.data

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val profileImageUrl: String? = null
)