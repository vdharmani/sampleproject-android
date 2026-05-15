package com.vdharmani.starter.feature.profile.presentation.profile

import android.net.Uri
import com.vdharmani.starter.feature.auth.domain.model.User

data class ProfileUiState(
    val user: User? = null,
    val localAvatarUri: Uri? = null,
    val isLoggingOut: Boolean = false,
)
