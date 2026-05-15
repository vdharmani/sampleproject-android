package com.sample.app.feature.profile.presentation.profile

import android.net.Uri
import com.sample.app.feature.auth.domain.model.User

data class ProfileUiState(
    val user: User? = null,
    val localAvatarUri: Uri? = null,
    val isLoggingOut: Boolean = false,
)
