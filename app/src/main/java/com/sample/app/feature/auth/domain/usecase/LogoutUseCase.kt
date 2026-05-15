package com.sample.app.feature.auth.domain.usecase

import com.sample.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): Result<Unit> = repository.logout()
}
