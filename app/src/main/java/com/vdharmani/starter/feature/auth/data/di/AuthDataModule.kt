package com.vdharmani.starter.feature.auth.data.di

import com.vdharmani.starter.core.network.interceptor.TokenRefresher
import com.vdharmani.starter.feature.auth.data.remote.AuthApi
import com.vdharmani.starter.feature.auth.data.repository.AuthRepositoryImpl
import com.vdharmani.starter.feature.auth.data.repository.AuthTokenRefresher
import com.vdharmani.starter.feature.auth.domain.repository.AuthRepository
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
