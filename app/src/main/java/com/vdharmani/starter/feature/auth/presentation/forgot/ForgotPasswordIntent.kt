package com.vdharmani.starter.feature.auth.presentation.forgot

sealed interface ForgotPasswordIntent {
    data class EmailChanged(val value: String) : ForgotPasswordIntent
    data object Submit : ForgotPasswordIntent
    data object BackToLogin : ForgotPasswordIntent
}
