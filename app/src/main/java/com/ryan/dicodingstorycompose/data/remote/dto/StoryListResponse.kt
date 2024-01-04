package com.ryan.dicodingstorycompose.data.remote.dto

data class StoryListResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<StoryDto>
)