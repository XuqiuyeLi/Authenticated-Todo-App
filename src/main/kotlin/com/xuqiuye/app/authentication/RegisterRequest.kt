package com.xuqiuye.app.authentication

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)