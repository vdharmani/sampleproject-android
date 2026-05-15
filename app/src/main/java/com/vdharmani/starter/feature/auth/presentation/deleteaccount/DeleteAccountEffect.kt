package com.vdharmani.starter.feature.auth.presentation.deleteaccount

sealed interface DeleteAccountEffect {
    /** Account deleted; caller should clear back stack and route to Login. */
    data object NavigateToLogin : DeleteAccountEffect
    data class ShowError(val message: String) : DeleteAccountEffect
}
