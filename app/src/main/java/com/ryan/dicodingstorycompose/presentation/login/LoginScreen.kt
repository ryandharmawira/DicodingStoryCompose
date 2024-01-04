package com.ryan.dicodingstorycompose.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryan.dicodingstorycompose.R
import com.ryan.dicodingstorycompose.common.components.LoadingDialog
import com.ryan.dicodingstorycompose.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onRegisterClick: () -> Unit,
    onLoggedIn: () -> Unit,
) {

    LoginScreenContent(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        onRegisterClick = onRegisterClick,
        onLoggedIn = onLoggedIn
    )
}

@Composable
fun LoginScreenContent(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    onRegisterClick: () -> Unit,
    onLoggedIn: () -> Unit,
) {

    LaunchedEffect(key1 = state.isLoggedIn) {
        if (state.isLoggedIn) onLoggedIn()
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.4f)
                        .padding(16.dp),
                    painter = painterResource(id = R.drawable.image_dicoding),
                    contentDescription = null,
                    colorFilter = if (isSystemInDarkTheme())
                        ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                    else
                        null
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.emailInput,
                    onValueChange = { onEvent(LoginEvent.OnEmailChange(it)) },
                    label = { Text(text = stringResource(id = R.string.email)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.passwordInput,
                    onValueChange = { onEvent(LoginEvent.OnPasswordChange(it)) },
                    label = { Text(text = stringResource(id = R.string.password)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                    },
                    trailingIcon = {
                        val image = if (state.passwordVisible)
                            Icons.Default.Visibility
                        else Icons.Default.VisibilityOff

                        // Localized description for accessibility services
                        val description = if (state.passwordVisible)
                            stringResource(id = R.string.hide_password)
                        else stringResource(id = R.string.show_password)

                        IconButton(
                            onClick = { onEvent(LoginEvent.OnPasswordVisibilityChange) }
                        ) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (state.passwordVisible)
                        VisualTransformation.None
                    else PasswordVisualTransformation(),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onEvent(LoginEvent.OnLoginClick) },
                    enabled = state.isValidInput
                ) {
                    Text(text = stringResource(id = R.string.login))
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRegisterClick,
                ) {
                    Text(text = stringResource(id = R.string.register))
                }
            }
            LoadingDialog(isShowingDialog = state.isLoading)
            state.errorMessage?.let {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = it,
                        duration = SnackbarDuration.Short
                    )
                }
                onEvent(LoginEvent.OnErrorMessageShown)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AppTheme {
        LoginScreenContent(
            state = LoginState(),
            onEvent = {},
            onRegisterClick = {},
            onLoggedIn = {}
        )
    }
}