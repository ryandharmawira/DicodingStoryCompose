package com.ryan.dicodingstorycompose.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.ryan.dicodingstorycompose.common.Resource
import com.ryan.dicodingstorycompose.data.session.Session
import com.ryan.dicodingstorycompose.domain.repository.StoryRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val session: Session,
    private val repository: StoryRepository,
) : ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    init {
        viewModelScope.launch {
            session.getTokenFlow().collect { token ->
                if (token.isBlank()) {
                    state = state.copy(isLoggedIn = false)
                }
            }
        }
        getStories()
    }

    fun getStories() = viewModelScope.launch {
        repository.getStories().collect { result ->
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
}