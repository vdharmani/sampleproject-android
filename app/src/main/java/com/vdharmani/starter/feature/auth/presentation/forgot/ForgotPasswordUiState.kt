package com.vdharmani.starter.feature.auth.presentation.forgot

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    /** Flips to `true` after the API call succeeds, so the screen can swap
     *  to a "check your inbox" confirmation state. */
    val sent: Boolean = false,
)
