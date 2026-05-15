package com.vdharmani.starter.core.datastore

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists the auth tokens, **encrypted at rest**.
 *
 * Backed by [EncryptedSharedPreferences] (Android Keystore + AES-256-GCM).
 * The previous DataStore Preferences impl stored tokens in plain XML — fine
 * for non-sensitive prefs, but bad for credentials. This is what's safe to
 * sit on disk while the app is uninstalled-but-cached, or to keep around
 * if a user's device is later compromised.
 *
 * Junior tip: read/write are suspend (off the main thread); the
 * [authTokenFlow] is a MutableStateFlow we update manually so emissions
 * stay synchronous with save/clear.
 */
@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    private val _authTokenFlow = MutableStateFlow(readSnapshot())
    val authTokenFlow: Flow<StoredAuthToken?> = _authTokenFlow.asStateFlow()

    /** One-shot read for callers that don't need to observe (e.g. interceptors). */
    suspend fun read(): StoredAuthToken? = authTokenFlow.first()

    suspend fun save(accessToken: String, refreshToken: String) {
        withContext(Dispatchers.IO) {
            prefs.edit()
                .putString(KEY_ACCESS, accessToken)
                .putString(KEY_REFRESH, refreshToken)
                .apply()
        }
        _authTokenFlow.value = StoredAuthToken(accessToken, refreshToken)
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) { prefs.edit().clear().apply() }
        _authTokenFlow.value = null
    }

    private fun readSnapshot(): StoredAuthToken? {
        val access = prefs.getString(KEY_ACCESS, null)
        val refresh = prefs.getString(KEY_REFRESH, null)
        return if (access.isNullOrEmpty()) null
        else StoredAuthToken(access, refresh.orEmpty())
    }

    private companion object {
        const val FILE_NAME = "starter_auth_secure"
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
    }
}

data class StoredAuthToken(val accessToken: String, val refreshToken: String)
