package com.ryan.dicodingstorycompose.presentation.register

sealed class RegisterEvent {
    data class OnNameChange(val name: String): RegisterEvent()
    data class OnEmailChange(val email: String) : RegisterEvent()
    data class OnPasswordChange(val password: String) : RegisterEvent()
    object OnPasswordVisibilityChange : RegisterEvent()
    object OnRegisterClick : RegisterEvent()
    object OnErrorMessageShown : RegisterEvent()
    object OnDismissDialog : RegisterEvent()
}