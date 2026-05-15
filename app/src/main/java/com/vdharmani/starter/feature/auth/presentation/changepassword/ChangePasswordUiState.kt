package com.vdharmani.starter.feature.auth.presentation.changepassword

data class ChangePasswordUiState(
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val done: Boolean = false,
)
