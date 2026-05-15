package com.sample.app.feature.auth.presentation.changepassword

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
import com.sample.app.R
import com.sample.app.core.ui.components.AppLoader
import com.sample.app.core.ui.components.PasswordField

@Composable
fun ChangePasswordScreen(onBack: () -> Unit, viewModel: ChangePasswordViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { fx ->
            when (fx) {
                is ChangePasswordEffect.ShowError -> snackbarHostState.showSnackbar(fx.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.done) {
                DoneConfirmation(onBack = onBack)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.auth_change_title), style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(32.dp))

                    PasswordField(
                        value = state.oldPassword,
                        onValueChange = { viewModel.handle(ChangePasswordIntent.OldPasswordChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(R.string.auth_change_current)
                    )
                    Spacer(Modifier.height(16.dp))
                    PasswordField(
                        value = state.newPassword,
                        onValueChange = { viewModel.handle(ChangePasswordIntent.NewPasswordChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(R.string.auth_change_new)
                    )
                    Spacer(Modifier.height(16.dp))
                    PasswordField(
                        value = state.confirmPassword,
                        onValueChange = { viewModel.handle(ChangePasswordIntent.ConfirmPasswordChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(R.string.auth_change_confirm)
                    )
                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.handle(ChangePasswordIntent.Submit) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    ) { Text(stringResource(R.string.auth_change_submit)) }
                    Spacer(Modifier.height(8.dp))

                    TextButton(onClick = onBack) { Text(stringResource(R.string.auth_cancel)) }
                }
            }
            if (state.isLoading) AppLoader()
        }
    }
}

@Composable
private fun DoneConfirmation(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.auth_change_done_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.auth_change_done_body),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.auth_back))
        }
    }
}
