package com.sample.app.feature.auth.presentation.forgot

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.app.core.ui.components.AppLoader
import com.sample.app.core.ui.components.EmailField
import com.sample.app.R

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ForgotPasswordEffect.ShowError ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    ForgotPasswordContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = { intent ->
            if (intent is ForgotPasswordIntent.BackToLogin) onBackToLogin()
            else viewModel.handle(intent)
        },
    )
}

@Composable
private fun ForgotPasswordContent(
    state: ForgotPasswordUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (ForgotPasswordIntent) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.sent) SentConfirmation(email = state.email, onBack = { onIntent(ForgotPasswordIntent.BackToLogin) })
            else ForgotPasswordForm(state = state, onIntent = onIntent)

            if (state.isLoading) AppLoader()
        }
    }
}

@Composable
private fun ForgotPasswordForm(
    state: ForgotPasswordUiState,
    onIntent: (ForgotPasswordIntent) -> Unit,
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
            text = stringResource(R.string.auth_forgot_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.auth_forgot_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))

        EmailField(
            value = state.email,
            onValueChange = { onIntent(ForgotPasswordIntent.EmailChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            error = state.emailError,
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { onIntent(ForgotPasswordIntent.Submit) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && state.email.isNotBlank(),
        ) {
            Text(stringResource(R.string.auth_forgot_submit))
        }
        Spacer(Modifier.height(8.dp))

        TextButton(onClick = { onIntent(ForgotPasswordIntent.BackToLogin) }) {
            Text(stringResource(R.string.auth_back_to_login))
        }
    }
}

@Composable
private fun SentConfirmation(email: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.auth_forgot_sent_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.auth_forgot_sent_body, email),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.auth_back_to_login))
        }
    }
}
