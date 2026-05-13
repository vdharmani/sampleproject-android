package com.vdharmani.starter.feature.auth.presentation.deleteaccount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import com.vdharmani.starter.feature.auth.R

@Composable
fun DeleteAccountScreen(
    onBack: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: DeleteAccountViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { fx ->
            when (fx) {
                DeleteAccountEffect.NavigateToLogin -> onAccountDeleted()
                is DeleteAccountEffect.ShowError -> snackbarHostState.showSnackbar(fx.message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    stringResource(R.string.auth_delete_title),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(R.string.auth_delete_body),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.handle(DeleteAccountIntent.RequestConfirm) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                    enabled = !state.isLoading,
                ) { Text(stringResource(R.string.auth_delete_submit)) }
                Spacer(Modifier.height(8.dp))

                TextButton(onClick = onBack) { Text(stringResource(R.string.auth_cancel)) }
            }
            if (state.isLoading) AppLoader()
        }

        if (state.confirmationShown) {
            AlertDialog(
                onDismissRequest = { viewModel.handle(DeleteAccountIntent.DismissConfirm) },
                title = { Text(stringResource(R.string.auth_delete_confirm_title)) },
                text = { Text(stringResource(R.string.auth_delete_confirm_body)) },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.handle(DeleteAccountIntent.ConfirmDelete) },
                    ) { Text(stringResource(R.string.auth_delete_confirm_yes), color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.handle(DeleteAccountIntent.DismissConfirm) },
                    ) { Text(stringResource(R.string.auth_cancel)) }
                },
            )
        }
    }
}
