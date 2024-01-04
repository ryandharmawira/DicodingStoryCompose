package com.ryan.dicodingstorycompose.presentation.add_story

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
class AddStoryViewModel @Inject constructor(
    private val repository: StoryRepository,
) : ViewModel() {

    var state by mutableStateOf(AddStoryState())
        private set

    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }

    }

    fun onEvent(event: AddStoryEvent) {
        when (event) {
            is AddStoryEvent.OnDescriptionChange -> {
                state = state.copy(descriptionInput = event.description)
            }
            is AddStoryEvent.OnPhotoChange -> {
                state = state.copy(photoFile = event.photoFile)
            }
            is AddStoryEvent.OnLocationChange -> {
                state = state.copy(
                    lat = event.lat,
                    lon = event.lon
                )
            }
            AddStoryEvent.OnUploadClick -> {
                uploadStory()
            }
            AddStoryEvent.OnErrorMessageShown -> {
                state = state.copy(errorMessage = null)
            }
            AddStoryEvent.OnDismissDialog -> {
                state = state.copy(success = false)
            }
        }
        state = state.copy(
            isValidInput = state.photoFile != null && state.descriptionInput.isNotBlank()
        )
    }

    private fun uploadStory() {
        state.photoFile?.let { photoFile ->
            viewModelScope.launch {
                repository.addStory(
                    photoFile,
                    state.descriptionInput,
                    state.lat,
                    state.lon
                ).collect { result ->
                    state = when (result) {
                        is Resource.Loading -> {
                            state.copy(isLoading = true)
                        }
                        is Resource.Error -> {
                            state.copy(isLoading = false, errorMessage = result.message)
                        }
                        is Resource.Success -> {
                            state.copy(isLoading = false, success = true)
                        }
                    }
                }
            }
        }
    }
}