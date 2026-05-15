package com.sample.app.feature.auth.domain.usecase

import com.sample.app.feature.auth.domain.AuthValidation
import com.sample.app.feature.auth.domain.EmptyFieldException
import com.sample.app.feature.auth.domain.InvalidEmailException
import com.sample.app.feature.auth.domain.WeakPasswordException
import com.sample.app.feature.auth.domain.model.AuthToken
import com.sample.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Validate inputs then ask the repository to log in.
 *
 * Validation lives here (not in the ViewModel) so the same rules apply to
 * every entry point — login form, deep-link auto-login, "remember me"
 * restore, etc. — without rewriting them per screen.
 */
class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<AuthToken> {
        if (email.isBlank()) return Result.failure(EmptyFieldException("Email"))
        if (!email.contains("@") || !email.contains(".")) {
            return Result.failure(InvalidEmailException())
        }
        if (password.isBlank()) return Result.failure(EmptyFieldException("Password"))
        if (password.length < AuthValidation.MIN_PASSWORD_LENGTH) {
            return Result.failure(WeakPasswordException())
        }
        return repository.login(email.trim(), password)
    }
}
