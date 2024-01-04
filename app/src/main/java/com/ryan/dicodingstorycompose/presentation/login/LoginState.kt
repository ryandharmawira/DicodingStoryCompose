package com.ryan.dicodingstorycompose.presentation.login

data class LoginState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emailInput: String = "",
    val passwordInput: String = "",
    val passwordVisible: Boolean = false,
    val isValidInput: Boolean = false,
)