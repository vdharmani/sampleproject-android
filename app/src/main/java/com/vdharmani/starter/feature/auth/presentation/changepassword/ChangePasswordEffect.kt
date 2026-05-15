package com.vdharmani.starter.feature.auth.presentation.changepassword

sealed interface ChangePasswordEffect {
    data class ShowError(val message: String) : ChangePasswordEffect
}
