package com.vdharmani.starter.feature.auth.presentation.login

/**
 * One-shot side effects from the ViewModel to the screen — navigation, toasts,
 * snackbars. Effects are NOT part of UiState because they don't survive
 * recomposition (e.g. you don't want a "navigated to home" toast firing
 * twice on rotation). Delivered via a Channel.
 */
sealed interface LoginEffect {
    data object NavigateToHome : LoginEffect
    data class ShowError(val message: String) : LoginEffect
}
