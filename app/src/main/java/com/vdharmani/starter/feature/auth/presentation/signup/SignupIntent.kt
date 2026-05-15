package com.vdharmani.starter.feature.auth.presentation.signup

sealed interface SignupIntent {
    data class NameChanged(val value: String) : SignupIntent
    data class EmailChanged(val value: String) : SignupIntent
    data class PasswordChanged(val value: String) : SignupIntent
    data object Submit : SignupIntent
    data object BackToLogin : SignupIntent
}
