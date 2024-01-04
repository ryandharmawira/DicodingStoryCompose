package com.ryan.dicodingstorycompose.domain.repository

import com.ryan.dicodingstorycompose.common.Resource
import com.ryan.dicodingstorycompose.domain.model.Story
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StoryRepository {
    fun getStories(
        hasLocation: Int = 0,
    ): Flow<Resource<List<Story>>>
    fun addStory(
        photoFile: File,
        description: String,
        lat: Double? = null,
        lon: Double? = null,
    ): Flow<Resource<String>>
}