package com.vdharmani.starter.feature.auth.presentation.changepassword

sealed interface ChangePasswordIntent {
    data class OldPasswordChanged(val value: String) : ChangePasswordIntent
    data class NewPasswordChanged(val value: String) : ChangePasswordIntent
    data class ConfirmPasswordChanged(val value: String) : ChangePasswordIntent
    data object Submit : ChangePasswordIntent
    data object Back : ChangePasswordIntent
}
