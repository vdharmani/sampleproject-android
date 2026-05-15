package com.vdharmani.starter.feature.auth.domain.repository

import com.vdharmani.starter.feature.auth.domain.model.AuthState
import com.vdharmani.starter.feature.auth.domain.model.AuthToken
import com.vdharmani.starter.feature.auth.domain.model.ResetToken
import com.vdharmani.starter.feature.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Domain contract — pure Kotlin. The data layer implements this; presentation
 * never depends on the implementation, only the interface.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthToken>
    suspend fun signup(email: String, password: String, name: String): Result<AuthToken>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun verifyOtp(email: String, otp: String): Result<ResetToken>
    suspend fun resetPassword(token: ResetToken, newPassword: String): Result<Unit>
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>

    /** Observe current sign-in state. Emits SignedOut → SignedIn(user) → SignedOut. */
    fun observeAuthState(): Flow<AuthState>

    /** One-shot current user lookup for screens that don't need to observe. */
    suspend fun currentUser(): User?
}
