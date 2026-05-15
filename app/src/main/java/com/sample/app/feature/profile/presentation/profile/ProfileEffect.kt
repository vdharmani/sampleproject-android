package com.sample.app.feature.profile.presentation.profile

sealed interface ProfileEffect {
    data object NavigateToLogin : ProfileEffect
    data class ShowError(val message: String) : ProfileEffect
}
