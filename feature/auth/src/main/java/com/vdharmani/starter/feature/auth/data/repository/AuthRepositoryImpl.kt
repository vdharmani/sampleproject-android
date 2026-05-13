package com.vdharmani.starter.feature.auth.data.repository

import com.vdharmani.starter.core.database.dao.UserDao
import com.vdharmani.starter.core.datastore.TokenStore
import com.vdharmani.starter.core.network.apiCall
import com.vdharmani.starter.feature.auth.data.mapper.toAuthToken
import com.vdharmani.starter.feature.auth.data.mapper.toDomain
import com.vdharmani.starter.feature.auth.data.mapper.toEntity
import com.vdharmani.starter.feature.auth.data.mapper.toUserOrNull
import com.vdharmani.starter.feature.auth.data.remote.AuthApi
import com.vdharmani.starter.feature.auth.data.remote.dto.ChangePasswordRequestDto
import com.vdharmani.starter.feature.auth.data.remote.dto.ForgotPasswordRequestDto
import com.vdharmani.starter.feature.auth.data.remote.dto.LoginRequestDto
import com.vdharmani.starter.feature.auth.data.remote.dto.ResetPasswordRequestDto
import com.vdharmani.starter.feature.auth.data.remote.dto.SignupRequestDto
import com.vdharmani.starter.feature.auth.data.remote.dto.VerifyOtpRequestDto
import com.vdharmani.starter.feature.auth.domain.model.AuthState
import com.vdharmani.starter.feature.auth.domain.model.AuthToken
import com.vdharmani.starter.feature.auth.domain.model.ResetToken
import com.vdharmani.starter.feature.auth.domain.model.User
import com.vdharmani.starter.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenStore: TokenStore,
    private val userDao: UserDao,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthToken> = apiCall {
        val response = api.login(LoginRequestDto(email, password))
        val token = response.toAuthToken()
        tokenStore.save(token.accessToken, token.refreshToken)
        response.toUserOrNull()?.let { userDao.upsert(it.toEntity()) }
            ?: userDao.upsert(User(id = email, email = email, name = email.substringBefore('@')).toEntity())
        token
    }

    override suspend fun signup(email: String, password: String, name: String): Result<AuthToken> =
        apiCall {
            val response = api.signup(SignupRequestDto(email, password, name))
            val token = response.toAuthToken()
            tokenStore.save(token.accessToken, token.refreshToken)
            response.toUserOrNull()?.let { userDao.upsert(it.toEntity()) }
                ?: userDao.upsert(User(id = email, email = email, name = name).toEntity())
            token
        }

    override suspend fun forgotPassword(email: String): Result<Unit> = apiCall {
        api.forgotPassword(ForgotPasswordRequestDto(email))
    }

    override suspend fun verifyOtp(email: String, otp: String): Result<ResetToken> = apiCall {
        ResetToken(api.verifyOtp(VerifyOtpRequestDto(email, otp)).resetToken)
    }

    override suspend fun resetPassword(token: ResetToken, newPassword: String): Result<Unit> =
        apiCall {
            api.resetPassword(ResetPasswordRequestDto(token.value, newPassword))
        }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> =
        apiCall {
            api.changePassword(ChangePasswordRequestDto(oldPassword, newPassword))
        }

    override suspend fun logout(): Result<Unit> = apiCall {
        apiCall { api.logout() } // best-effort server-side invalidation
        tokenStore.clear()
        userDao.clear()
    }

    override suspend fun deleteAccount(): Result<Unit> = apiCall {
        api.deleteAccount()
        tokenStore.clear()
        userDao.clear()
    }

    override fun observeAuthState(): Flow<AuthState> =
        tokenStore.authTokenFlow.combine(userDao.observeCurrent()) { token, userEntity ->
            if (token == null) AuthState.SignedOut
            else AuthState.SignedIn(userEntity?.toDomain() ?: User(id = "", email = "", name = ""))
        }

    override suspend fun currentUser(): User? {
        // For demo: read first stored user.
        return null
    }
}
