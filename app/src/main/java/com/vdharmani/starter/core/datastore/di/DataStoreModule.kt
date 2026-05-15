package com.vdharmani.starter.core.datastore.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * TokenStore is `@Singleton` with `@Inject constructor`, so Hilt provides it
 * automatically. This module is a placeholder for future store bindings (e.g.
 * UserPreferencesStore) — leave it so juniors have a clear place to add them.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule
