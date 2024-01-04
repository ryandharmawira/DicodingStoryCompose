package com.ryan.dicodingstorycompose.presentation.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.ryan.dicodingstorycompose.common.Resource
import com.ryan.dicodingstorycompose.domain.repository.StoryRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: StoryRepository,
) : ViewModel() {

    var state by mutableStateOf(MapState())
        private set

    init {
        getStories()
    }

    private fun getStories() = viewModelScope.launch {
        repository.getStories(
            hasLocation = 1,
        ).collect { result ->
            state = when (result) {
                is Resource.Loading -> {
                    state.copy(isLoading = true)
                }
                is Resource.Error -> {
                    state.copy(isLoading = false, errorMessage = result.message)
                }
                is Resource.Success -> {
                    state.copy(isLoading = false, stories = result.data ?: emptyList())
                }
            }
        }
    }

    fun onLocationGranted(isGranted: Boolean) {
        state = state.copy(isLocationEnabled = isGranted)
    }

    fun onErrorMessageShown() {
        state = state.copy(errorMessage = null)
    }
}