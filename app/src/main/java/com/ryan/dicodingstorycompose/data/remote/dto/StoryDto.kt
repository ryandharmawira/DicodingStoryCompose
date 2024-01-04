package com.ryan.dicodingstorycompose.data.remote.dto

data class StoryDto(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double?,
    val lon: Double?,
)