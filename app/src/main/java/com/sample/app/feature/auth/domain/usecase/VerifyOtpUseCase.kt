package com.sample.app.feature.auth.domain.usecase

import com.sample.app.feature.auth.domain.AuthValidation
import com.sample.app.feature.auth.domain.InvalidOtpException
import com.sample.app.feature.auth.domain.model.ResetToken
import com.sample.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, otp: String): Result<ResetToken> {
        if (otp.length !in AuthValidation.OTP_MIN_LENGTH..AuthValidation.OTP_MAX_LENGTH ||
            !otp.all { it.isDigit() }
        ) {
            return Result.failure(InvalidOtpException())
        }
        return repository.verifyOtp(email, otp)
    }
}
