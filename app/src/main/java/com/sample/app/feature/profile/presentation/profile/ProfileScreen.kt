package com.sample.app.feature.profile.presentation.profile

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vdharmani.imagepicker.ImagePickerConfig
import com.vdharmani.imagepicker.compose.composeImagePicker
import com.sample.app.core.ui.components.AppLoader
import com.sample.app.R

@Composable
fun ProfileScreen(
    onLoggedOut: () -> Unit,
    onChangePassword: () -> Unit,
    onDeleteAccount: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { fx ->
            when (fx) {
                ProfileEffect.NavigateToLogin -> onLoggedOut()
                is ProfileEffect.ShowError -> snackbarHostState.showSnackbar(fx.message)
            }
        }
    }

    // imagepicker-android — composable handle, automatically scoped to this
    // composition. The lambda returns the processed (downscaled + EXIF-correct)
    // file URI ready for upload.
    val picker = composeImagePicker(
        authority = "${context.packageName}.provider",
        config = remember { ImagePickerConfig() },
        onPicked = { uri: Uri -> viewModel.handle(ProfileIntent.AvatarPicked(uri)) },
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AvatarBubble(uri = state.localAvatarUri)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { picker.captureImage() }) {
                        Text(stringResource(R.string.profile_avatar_camera))
                    }
                    OutlinedButton(onClick = { picker.uploadImage() }) {
                        Text(stringResource(R.string.profile_avatar_gallery))
                    }
                }
                Spacer(Modifier.height(32.dp))

                state.user?.let { user ->
                    Text(user.name, style = MaterialTheme.typography.titleLarge)
                    Text(user.email, style = MaterialTheme.typography.bodyMedium)
                } ?: Text(stringResource(R.string.profile_loading), style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(32.dp))
                HorizontalDivider()
                Spacer(Modifier.height(24.dp))

                OutlinedButton(
                    onClick = onChangePassword,
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(stringResource(R.string.profile_change_password)) }
                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { viewModel.handle(ProfileIntent.Logout) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoggingOut,
                ) { Text(stringResource(R.string.profile_log_out)) }
                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = onDeleteAccount,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) { Text(stringResource(R.string.profile_delete_account)) }
            }
            if (state.isLoggingOut) AppLoader()
        }
    }
}

@Composable
private fun AvatarBubble(uri: Uri?) {
    Surface(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        if (uri == null) {
            Box(contentAlignment = Alignment.Center) {
                Text("👤", style = MaterialTheme.typography.displayLarge)
            }
        } else {
            AsyncImage(
                model = uri,
                contentDescription = stringResource(R.string.profile_avatar_content_description),
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Crop,
            )
        }
    }
}
