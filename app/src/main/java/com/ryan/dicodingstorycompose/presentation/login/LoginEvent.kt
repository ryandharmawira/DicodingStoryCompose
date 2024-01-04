package com.ryan.dicodingstorycompose.presentation.login

sealed class LoginEvent {
    data class OnEmailChange(val email: String) : LoginEvent()
    data class OnPasswordChange(val password: String) : LoginEvent()
    object OnPasswordVisibilityChange : LoginEvent()
    object OnLoginClick : LoginEvent()
    object OnErrorMessageShown : LoginEvent()
}