package com.sample.app.feature.auth.presentation.forgot

sealed interface ForgotPasswordEffect {
    data class ShowError(val message: String) : ForgotPasswordEffect
}
