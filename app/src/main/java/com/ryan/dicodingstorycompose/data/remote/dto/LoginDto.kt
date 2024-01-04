package com.ryan.dicodingstorycompose.data.remote.dto

data class LoginDto(
    val userId: String,
    val name: String,
    val token: String,
)