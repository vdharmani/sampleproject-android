package com.vdharmani.starter.feature.auth.domain.usecase

import com.vdharmani.starter.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): Result<Unit> = repository.deleteAccount()
}
