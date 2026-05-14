package com.vdharmani.starter.feature.auth.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vdharmani.starter.core.ui.components.AppLoader
import com.vdharmani.starter.core.ui.components.EmailField
import com.vdharmani.starter.core.ui.components.PasswordField
import com.vdharmani.starter.feature.auth.R

/**
 * Login screen — connects the ViewModel to the dumb [LoginContent] composable.
 * Junior pattern: a "Screen" is the stateful entry, "Content" is the stateless
 * preview-friendly inner composable.
 */
@Composable
fun LoginScreen(
    onAuthed: () -> Unit,
    onGoSignup: () -> Unit,
    onGoForgot: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToHome -> onAuthed()
                is LoginEffect.ShowError ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    LoginContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = { intent ->
            when (intent) {
                LoginIntent.GoToSignup -> onGoSignup()
                LoginIntent.GoToForgotPassword -> onGoForgot()
                else -> viewModel.handle(intent)
            }
        },
    )
}

@Composable
private fun LoginContent(
    state: LoginUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (LoginIntent) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            LoginForm(state = state, onIntent = onIntent, padding = padding)
            if (state.isLoading) AppLoader()
        }
    }
}

@Composable
private fun LoginForm(
    state: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
    padding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.auth_login_title),
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.auth_login_subtitle),
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))

        EmailField(
            value = state.email,
            onValueChange = { onIntent(LoginIntent.EmailChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            error = state.emailError,
        )
        Spacer(Modifier.height(16.dp))

        PasswordField(
            value = state.password,
            onValueChange = { onIntent(LoginIntent.PasswordChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            error = state.passwordError,
        )
        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = { onIntent(LoginIntent.GoToForgotPassword) },
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(stringResource(R.string.auth_login_forgot))
        }
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { onIntent(LoginIntent.Submit) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
        ) {
            Text(stringResource(R.string.auth_login_submit))
        }
        Spacer(Modifier.height(16.dp))

        TextButton(onClick = { onIntent(LoginIntent.GoToSignup) }) {
            Text(stringResource(R.string.auth_login_go_signup))
        }
    }
}
