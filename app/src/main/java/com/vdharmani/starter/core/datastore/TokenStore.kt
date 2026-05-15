package com.vdharmani.starter.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vdharmani.starter.core.security.KeystoreCrypto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists the auth tokens, **encrypted at rest**.
 *
 * Storage is DataStore Preferences; each token string is encrypted with
 * [KeystoreCrypto] (AES-256-GCM, key held in the Android Keystore) before it
 * is written, so the on-disk file never contains plaintext credentials.
 *
 * This replaces the previous EncryptedSharedPreferences implementation — that
 * whole library (`androidx.security:security-crypto`) was deprecated by Google
 * with no drop-in successor; Keystore + DataStore is the recommended path.
 *
 * Junior tip: reads/writes are `suspend` (DataStore is async by design), and
 * [authTokenFlow] reflects every write automatically — no manual emission.
 */
@Singleton
class TokenStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val crypto: KeystoreCrypto,
) {

    val authTokenFlow: Flow<StoredAuthToken?> = dataStore.data
        .catch { cause ->
            // A corrupt/missing file should read as "no tokens", not crash.
            if (cause is IOException) emit(emptyPreferences()) else throw cause
        }
        .map { prefs ->
            val access = prefs[KEY_ACCESS]?.let(::decryptOrNull)
            val refresh = prefs[KEY_REFRESH]?.let(::decryptOrNull)
            if (access.isNullOrEmpty()) null
            else StoredAuthToken(access, refresh.orEmpty())
        }

    /** One-shot read for callers that don't need to observe (e.g. interceptors). */
    suspend fun read(): StoredAuthToken? = authTokenFlow.first()

    suspend fun save(accessToken: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[KEY_ACCESS] = crypto.encrypt(accessToken)
            prefs[KEY_REFRESH] = crypto.encrypt(refreshToken)
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    /** Decrypt defensively — a key reset or tampered value yields null, not a crash. */
    private fun decryptOrNull(value: String): String? =
        runCatching { crypto.decrypt(value) }.getOrNull()

    private companion object {
        val KEY_ACCESS = stringPreferencesKey("access_token")
        val KEY_REFRESH = stringPreferencesKey("refresh_token")
    }
}

data class StoredAuthToken(val accessToken: String, val refreshToken: String)
