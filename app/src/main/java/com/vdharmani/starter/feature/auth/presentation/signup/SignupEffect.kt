package com.vdharmani.starter.feature.auth.presentation.signup

sealed interface SignupEffect {
    data object NavigateToHome : SignupEffect
    data class ShowError(val message: String) : SignupEffect
}
