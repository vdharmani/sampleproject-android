package com.sample.app.feature.auth.domain.usecase

import com.sample.app.feature.auth.domain.AuthValidation
import com.sample.app.feature.auth.domain.PasswordsDontMatchException
import com.sample.app.feature.auth.domain.WeakPasswordException
import com.sample.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(oldPassword: String, newPassword: String, confirmPassword: String): Result<Unit> {
        if (newPassword != confirmPassword) return Result.failure(PasswordsDontMatchException())
        if (newPassword.length < AuthValidation.MIN_PASSWORD_LENGTH) {
            return Result.failure(WeakPasswordException())
        }
        return repository.changePassword(oldPassword, newPassword)
    }
}
