package com.sample.app.feature.auth.presentation.changepassword

sealed interface ChangePasswordEffect {
    data class ShowError(val message: String) : ChangePasswordEffect
}
