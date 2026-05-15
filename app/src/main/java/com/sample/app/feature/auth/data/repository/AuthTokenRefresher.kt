package com.sample.app.feature.auth.data.repository

import com.sample.app.core.datastore.StoredAuthToken
import com.sample.app.core.datastore.TokenStore
import com.sample.app.core.network.interceptor.TokenRefresher
import com.sample.app.feature.auth.data.mapper.toAuthToken
import com.sample.app.feature.auth.data.mapper.toStored
import com.sample.app.feature.auth.data.remote.AuthApi
import com.sample.app.feature.auth.data.remote.dto.RefreshTokenRequestDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wires `core:network`'s [TokenRefresher] SPI to the feature's [AuthApi].
 * Lives here so `core:network` doesn't depend on auth-specific types.
 */
@Singleton
class AuthTokenRefresher @Inject constructor(private val api: AuthApi, private val tokenStore: TokenStore) :
    TokenRefresher {

    override suspend fun refresh(refreshToken: String): StoredAuthToken? {
        // No refresh token to use — give up immediately.
        if (refreshToken.isBlank()) {
            tokenStore.clear()
            return null
        }
        return runCatching {
            val response = api.refresh(RefreshTokenRequestDto(refreshToken))
            val newToken = response.toAuthToken().toStored()
            tokenStore.save(newToken.accessToken, newToken.refreshToken)
            newToken
        }.getOrElse {
            tokenStore.clear()
            null
        }
    }
}
