package com.vdharmani.starter.feature.auth.presentation.login

/** User-driven inputs to the LoginScreen. The screen only sends Intents; it
 *  never mutates state directly. */
sealed interface LoginIntent {
    data class EmailChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object Submit : LoginIntent
    data object GoToSignup : LoginIntent
    data object GoToForgotPassword : LoginIntent
}
