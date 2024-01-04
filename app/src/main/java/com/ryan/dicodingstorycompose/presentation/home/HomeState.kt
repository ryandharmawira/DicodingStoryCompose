package com.ryan.dicodingstorycompose.presentation.home

import com.ryan.dicodingstorycompose.domain.model.Story

data class HomeState(
    val isLoggedIn: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val stories: List<Story> = emptyList()
)