package com.vdharmani.starter.core.network.interceptor

import com.vdharmani.starter.core.datastore.TokenStore
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles 401 responses by attempting a single refresh-then-retry.
 *
 * The actual refresh call is delegated via [TokenRefresher] so this module
 * doesn't depend on `feature:auth`. The feature module wires the refresher
 * via a Hilt binding at startup.
 *
 * Junior tip: never hold a refresh in flight twice. If two requests both 401
 * concurrently, OkHttp serializes calls into [authenticate] per host, but
 * we also gate via a `@Synchronized` block here so a buggy backend can't
 * trigger two refreshes for the same expiry.
 */
@Singleton
class TokenRefreshAuthenticator @Inject constructor(
    private val tokenStore: TokenStore,
    // Lazy breaks the Dagger cycle: TokenRefresher's impl needs the AuthApi,
    // which needs Retrofit, which needs OkHttpClient — which needs us. The
    // refresher is only realized on the first 401, by which time the whole
    // graph is wired.
    private val refresher: Lazy<TokenRefresher>,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Already retried once — give up to avoid an infinite loop.
        if (response.priorResponse != null) return null

        val current = runBlocking { tokenStore.read() } ?: return null

        val refreshed = synchronized(this) {
            // Re-read in case another thread refreshed while we waited.
            val nowStored = runBlocking { tokenStore.read() }
            if (nowStored != null && nowStored.accessToken != current.accessToken) {
                nowStored
            } else {
                runBlocking { refresher.get().refresh(current.refreshToken) }
            }
        } ?: return null

        return response.request.newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", "Bearer ${refreshed.accessToken}")
            .build()
    }
}

/**
 * SPI for swapping the actual refresh call. Implemented in `feature:auth` so
 * `core:network` doesn't depend on auth-specific types.
 */
interface TokenRefresher {
    /**
     * @return the new token pair (and persists it via TokenStore as a
     * side-effect), or `null` if refresh failed — caller will treat that as
     * "give up and let the 401 propagate."
     */
    suspend fun refresh(refreshToken: String): com.vdharmani.starter.core.datastore.StoredAuthToken?
}
