package com.ryan.dicodingstorycompose.presentation.add_story

import java.io.File

sealed class AddStoryEvent {
    data class OnDescriptionChange(val description: String) : AddStoryEvent()
    data class OnPhotoChange(val photoFile: File) : AddStoryEvent()
    data class OnLocationChange(val lat: Double, val lon: Double) : AddStoryEvent()
    object OnUploadClick : AddStoryEvent()
    object OnErrorMessageShown : AddStoryEvent()
    object OnDismissDialog : AddStoryEvent()
}