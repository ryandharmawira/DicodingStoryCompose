package com.ryan.dicodingstorycompose.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.ryan.dicodingstorycompose.common.Constants.passwordRegex
import com.ryan.dicodingstorycompose.common.Constants.restrictTabEnterRegex
import com.ryan.dicodingstorycompose.common.Resource
import com.ryan.dicodingstorycompose.common.isValidEmail
import com.ryan.dicodingstorycompose.domain.repository.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
): ViewModel() {

    var state by mutableStateOf(RegisterState())
        private set

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnNameChange -> {
                if (event.name.isBlank() || event.name.matches(Regex(restrictTabEnterRegex))) {
                    state = state.copy(nameInput = event.name)
                }
            }
            is RegisterEvent.OnEmailChange -> {
                if (event.email.isBlank() || event.email.matches(Regex(restrictTabEnterRegex))) {
                    state = state.copy(emailInput = event.email)
                }
            }
            is RegisterEvent.OnPasswordChange -> {
                if (event.password.isBlank() || event.password.matches(Regex(passwordRegex))) {
                    state = state.copy(passwordInput = event.password)
                }
            }
            RegisterEvent.OnPasswordVisibilityChange -> {
                state = state.copy(passwordVisible = !state.passwordVisible)
            }
            RegisterEvent.OnRegisterClick -> { registerUser() }
            RegisterEvent.OnErrorMessageShown -> {
                state = state.copy(errorMessage = null)
            }
            RegisterEvent.OnDismissDialog -> {
                state = state.copy(isRegistered = false)
            }
        }
        state = state.copy(isValidInput = state.emailInput.isValidEmail() && state.passwordInput.length > 7)
    }

    private fun registerUser() = viewModelScope.launch {
        repository.registerUser(state.nameInput, state.emailInput, state.passwordInput).collect { result ->
            state = when (result) {
                is Resource.Loading -> {
                    state.copy(isLoading = true)
                }
                is Resource.Error -> {
                    state.copy(isLoading = false, errorMessage = result.message)
                }
                is Resource.Success -> {
                    state.copy(isLoading = false, isRegistered = true)
                }
            }
        }
    }
}