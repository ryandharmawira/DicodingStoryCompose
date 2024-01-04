package com.ryan.dicodingstorycompose.presentation.add_story

import java.io.File

data class AddStoryState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false,
    val descriptionInput: String = "",
    val photoFile: File? = null,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val isValidInput: Boolean = false,
)