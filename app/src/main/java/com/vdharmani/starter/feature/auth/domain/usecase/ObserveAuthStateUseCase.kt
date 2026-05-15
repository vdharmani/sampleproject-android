package com.vdharmani.starter.feature.auth.domain.usecase

import com.vdharmani.starter.feature.auth.domain.model.AuthState
import com.vdharmani.starter.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthState> = repository.observeAuthState()
}
