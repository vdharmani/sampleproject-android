package com.sample.app.feature.auth.domain.usecase

import com.sample.app.feature.auth.domain.model.AuthState
import com.sample.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveAuthStateUseCase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(): Flow<AuthState> = repository.observeAuthState()
}
