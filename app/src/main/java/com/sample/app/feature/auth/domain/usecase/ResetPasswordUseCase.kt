package com.sample.app.feature.auth.domain.usecase

import com.sample.app.feature.auth.domain.PasswordsDontMatchException
import com.sample.app.feature.auth.domain.WeakPasswordException
import com.sample.app.feature.auth.domain.model.ResetToken
import com.sample.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(
        token: ResetToken,
        newPassword: String,
        confirmPassword: String,
    ): Result<Unit> {
        if (newPassword != confirmPassword) return Result.failure(PasswordsDontMatchException())
        if (newPassword.length < 8) return Result.failure(WeakPasswordException())
        return repository.resetPassword(token, newPassword)
    }
}
