package com.sample.app.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Supplies the `DataStore<Preferences>` that backs
 * [com.sample.app.core.datastore.TokenStore]. `TokenStore` itself is
 * `@Inject`-constructed, so it isn't bound here.
 *
 * Junior tip: if you add a second store (e.g. UserPreferencesStore), give each
 * `DataStore<Preferences>` its own `@Qualifier` so Hilt can tell them apart.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideAuthDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("auth_tokens")
        }
}
