package com.vdharmani.starter.feature.auth.presentation.login

/** Everything the LoginScreen needs to render — and only that. */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
)
