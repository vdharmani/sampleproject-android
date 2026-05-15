package com.vdharmani.starter.core.network.interceptor

import com.vdharmani.starter.core.datastore.TokenStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Attaches `Authorization: Bearer <access>` to every outbound request if a
 * token is stored. Skips unauthenticated endpoints (login, signup, refresh)
 * — those carry the `No-Auth: true` header which is stripped before sending.
 *
 * `runBlocking` here is acceptable: OkHttp interceptors run on their own
 * dispatcher thread (the OkHttp dispatcher pool), never the main thread.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Endpoints marked No-Auth (login, signup, refresh) bypass token attach.
        if (request.header(HEADER_NO_AUTH) != null) {
            val stripped = request.newBuilder().removeHeader(HEADER_NO_AUTH).build()
            return chain.proceed(stripped)
        }

        val token = runBlocking { tokenStore.read() }
        val authed = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer ${token.accessToken}")
                .build()
        } else {
            request
        }
        return chain.proceed(authed)
    }

    companion object {
        const val HEADER_NO_AUTH = "No-Auth"
    }
}
