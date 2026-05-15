package com.vdharmani.starter.feature.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ---- Request bodies ---------------------------------------------------------

@Serializable
data class LoginRequestDto(val email: String, val password: String)

@Serializable
data class SignupRequestDto(val email: String, val password: String, val name: String? = null)

@Serializable
data class ForgotPasswordRequestDto(val email: String)

@Serializable
data class VerifyOtpRequestDto(val email: String, val otp: String)

@Serializable
data class ResetPasswordRequestDto(
    @SerialName("reset_token") val resetToken: String,
    @SerialName("new_password") val newPassword: String,
)

@Serializable
data class ChangePasswordRequestDto(
    @SerialName("old_password") val oldPassword: String,
    @SerialName("new_password") val newPassword: String,
)

@Serializable
data class RefreshTokenRequestDto(
    @SerialName("refresh_token") val refreshToken: String,
)

// ---- Response bodies --------------------------------------------------------

// reqres.in returns {"id": 4, "token": "QpwL5tke4Pnpja7X4"} — no refresh.
// We fake refreshToken = "" for the mock; real backends will fill in their
// own field name (update the mapper when switching).
@Serializable
data class AuthResponseDto(
    val id: Int? = null,
    val token: String,
    @SerialName("refresh_token") val refreshToken: String? = null,
    val email: String? = null,
    val name: String? = null,
    val avatar: String? = null,
)

@Serializable
data class VerifyOtpResponseDto(
    @SerialName("reset_token") val resetToken: String,
)
