package com.sample.app.feature.auth.domain.usecase

import com.sample.app.feature.auth.domain.EmptyFieldException
import com.sample.app.feature.auth.domain.InvalidEmailException
import com.sample.app.feature.auth.domain.WeakPasswordException
import com.sample.app.feature.auth.domain.model.AuthToken
import com.sample.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String, name: String): Result<AuthToken> {
        if (name.isBlank()) return Result.failure(EmptyFieldException("Name"))
        if (email.isBlank()) return Result.failure(EmptyFieldException("Email"))
        if (!email.contains("@") || !email.contains(".")) {
            return Result.failure(InvalidEmailException())
        }
        if (password.length < 8) return Result.failure(WeakPasswordException())
        return repository.signup(email.trim(), password, name.trim())
    }
}
