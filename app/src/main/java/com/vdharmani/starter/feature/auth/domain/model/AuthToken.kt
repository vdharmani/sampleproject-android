package com.vdharmani.starter.feature.auth.domain.model

/** Auth token pair as the domain understands it. */
data class AuthToken(val accessToken: String, val refreshToken: String)

/** Short-lived token returned by OTP verify, consumed by the reset call. */
@JvmInline
value class ResetToken(val value: String)

/** Coarse-grained "are we signed in?" signal that screens listen to. */
sealed class AuthState {
    data object SignedOut : AuthState()
    data class SignedIn(val user: User) : AuthState()
}
