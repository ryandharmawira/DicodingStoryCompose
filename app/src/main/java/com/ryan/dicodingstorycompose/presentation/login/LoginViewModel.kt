package com.ryan.dicodingstorycompose.presentation.login

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
import com.ryan.dicodingstorycompose.data.session.Session
import com.ryan.dicodingstorycompose.domain.repository.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val session: Session,
): ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    init {
        viewModelScope.launch {
            session.getTokenFlow().collect { token ->
                if (token.isNotBlank()) {
                    state = state.copy(isLoggedIn = true)
                }
            }
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChange -> {
                if (event.email.isBlank() || event.email.matches(Regex(restrictTabEnterRegex))) {
                    state = state.copy(emailInput = event.email)
                }
            }
            is LoginEvent.OnPasswordChange -> {
                if (event.password.isBlank() || event.password.matches(Regex(passwordRegex))) {
                    state = state.copy(passwordInput = event.password)
                }
            }
            LoginEvent.OnPasswordVisibilityChange -> {
                state = state.copy(passwordVisible = !state.passwordVisible)
            }
            LoginEvent.OnLoginClick -> { loginUser() }
            LoginEvent.OnErrorMessageShown -> {
                state = state.copy(errorMessage = null)
            }
        }
        state = state.copy(isValidInput = state.emailInput.isValidEmail() && state.passwordInput.length > 7)
    }

    private fun loginUser() = viewModelScope.launch {
        repository.loginUser(state.emailInput, state.passwordInput).collect { result ->
            state = when (result) {
                is Resource.Loading -> {
                    state.copy(isLoading = true)
                }
                is Resource.Error -> {
                    state.copy(isLoading = false, errorMessage = result.message)
                }
                is Resource.Success -> {
                    state.copy(isLoading = false, isLoggedIn = true)
                }
            }
        }
    }
}