package com.vdharmani.starter.feature.auth.domain.usecase

import com.vdharmani.starter.feature.auth.domain.InvalidEmailException
import com.vdharmani.starter.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (!email.contains("@") || !email.contains(".")) {
            return Result.failure(InvalidEmailException())
        }
        return repository.forgotPassword(email.trim())
    }
}
