package com.sample.app.feature.auth.data.di

import com.sample.app.core.network.interceptor.TokenRefresher
import com.sample.app.feature.auth.data.remote.AuthApi
import com.sample.app.feature.auth.data.repository.AuthRepositoryImpl
import com.sample.app.feature.auth.data.repository.AuthTokenRefresher
import com.sample.app.feature.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthDataModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTokenRefresher(impl: AuthTokenRefresher): TokenRefresher

    companion object {
        @Provides
        @Singleton
        fun provideAuthApi(retrofit: Retrofit): AuthApi =
            retrofit.create(AuthApi::class.java)
    }
}
