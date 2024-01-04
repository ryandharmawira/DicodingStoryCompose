package com.ryan.dicodingstorycompose.presentation.map

import com.ryan.dicodingstorycompose.domain.model.Story

data class MapState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val stories: List<Story> = emptyList(),
    val isLocationEnabled: Boolean = false,
)