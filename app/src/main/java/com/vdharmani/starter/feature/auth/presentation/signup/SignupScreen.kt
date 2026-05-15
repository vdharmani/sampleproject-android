package com.vdharmani.starter.feature.auth.presentation.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vdharmani.starter.core.ui.components.AppLoader
import com.vdharmani.starter.core.ui.components.EmailField
import com.vdharmani.starter.core.ui.components.PasswordField
import com.vdharmani.starter.R

@Composable
fun SignupScreen(
    onAuthed: () -> Unit,
    onBackToLogin: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                SignupEffect.NavigateToHome -> onAuthed()
                is SignupEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    SignupContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = { intent ->
            if (intent is SignupIntent.BackToLogin) onBackToLogin()
            else viewModel.handle(intent)
        },
    )
}

@Composable
private fun SignupContent(
    state: SignupUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (SignupIntent) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(stringResource(R.string.auth_signup_title), style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = state.name,
                    onValueChange = { onIntent(SignupIntent.NameChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.auth_signup_name)) },
                    singleLine = true,
                    isError = state.nameError != null,
                    supportingText = state.nameError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
                Spacer(Modifier.height(16.dp))

                EmailField(
                    value = state.email,
                    onValueChange = { onIntent(SignupIntent.EmailChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    error = state.emailError,
                )
                Spacer(Modifier.height(16.dp))

                PasswordField(
                    value = state.password,
                    onValueChange = { onIntent(SignupIntent.PasswordChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    error = state.passwordError,
                )
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { onIntent(SignupIntent.Submit) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                ) { Text(stringResource(R.string.auth_signup_submit)) }
                Spacer(Modifier.height(8.dp))

                TextButton(onClick = { onIntent(SignupIntent.BackToLogin) }) {
                    Text(stringResource(R.string.auth_signup_go_login))
                }
            }
            if (state.isLoading) AppLoader()
        }
    }
}
