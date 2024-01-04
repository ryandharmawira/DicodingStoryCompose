package com.ryan.dicodingstorycompose.data.remote.dto

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginDto?
)