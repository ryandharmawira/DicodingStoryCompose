package com.ryan.dicodingstorycompose.presentation.register

data class RegisterState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistered: Boolean = false,
    val nameInput: String = "",
    val emailInput: String = "",
    val passwordInput: String = "",
    val passwordVisible: Boolean = false,
    val isValidInput: Boolean = false,
)