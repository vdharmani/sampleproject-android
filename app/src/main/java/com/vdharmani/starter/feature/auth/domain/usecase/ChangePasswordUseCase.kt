package com.vdharmani.starter.feature.auth.domain.usecase

import com.vdharmani.starter.feature.auth.domain.PasswordsDontMatchException
import com.vdharmani.starter.feature.auth.domain.WeakPasswordException
import com.vdharmani.starter.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String,
    ): Result<Unit> {
        if (newPassword != confirmPassword) return Result.failure(PasswordsDontMatchException())
        if (newPassword.length < 8) return Result.failure(WeakPasswordException())
        return repository.changePassword(oldPassword, newPassword)
    }
}
