package com.vdharmani.starter.feature.profile.presentation.profile

import android.net.Uri

sealed interface ProfileIntent {
    data class AvatarPicked(val uri: Uri) : ProfileIntent
    data object Logout : ProfileIntent
    data object GoToChangePassword : ProfileIntent
    data object GoToDeleteAccount : ProfileIntent
    data object Back : ProfileIntent
}
