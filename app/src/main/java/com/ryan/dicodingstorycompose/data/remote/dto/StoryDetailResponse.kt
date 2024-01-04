package com.ryan.dicodingstorycompose.data.remote.dto

data class StoryDetailResponse(
    val error: Boolean,
    val message: String,
    val story: StoryDto
)