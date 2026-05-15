package com.vdharmani.starter.feature.auth.domain.usecase

import com.vdharmani.starter.feature.auth.domain.InvalidOtpException
import com.vdharmani.starter.feature.auth.domain.model.ResetToken
import com.vdharmani.starter.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, otp: String): Result<ResetToken> {
        if (otp.length !in 4..6 || !otp.all { it.isDigit() }) {
            return Result.failure(InvalidOtpException())
        }
        return repository.verifyOtp(email, otp)
    }
}
