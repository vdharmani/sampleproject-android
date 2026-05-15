package com.vdharmani.starter.feature.auth.data.remote

import com.vdharmani.starter.core.network.interceptor.AuthInterceptor
import com.vdharmani.starter.feature.auth.data.remote.dto.AuthResponseDto
import com.vdharmani.starter.feature.auth.data.remote.dto.ChangePasswordRequestDto
import com.vdharmani.starter.feature.auth.data.remote.dto.ForgotPasswordRequestDto
import com.vdharmani.starter.feature.auth.data.remote.dto.LoginRequestDto
import com.vdharmani.starter.feature.auth.data.remote.dto.RefreshTokenRequestDto
import com.vdharmani.starter.feature.auth.data.remote.dto.ResetPasswordRequestDto
import com.vdharmani.starter.feature.auth.data.remote.dto.SignupRequestDto
import com.vdharmani.starter.feature.auth.data.remote.dto.VerifyOtpRequestDto
import com.vdharmani.starter.feature.auth.data.remote.dto.VerifyOtpResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Retrofit interface pointed at reqres.in for demo purposes.
 *
 * The reqres.in surface only really supports login + signup; the other
 * endpoints will 404 until a junior swaps in a real backend. Endpoint paths
 * are kept in one place so swapping is "edit this file + BuildConfig.BASE_URL".
 *
 * NOTE: login/signup/refresh carry the `No-Auth` header so [AuthInterceptor]
 * doesn't try to attach a Bearer token on the way out.
 */
interface AuthApi {

    @Headers("${AuthInterceptor.HEADER_NO_AUTH}: true")
    @POST("login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @Headers("${AuthInterceptor.HEADER_NO_AUTH}: true")
    @POST("register")
    suspend fun signup(@Body request: SignupRequestDto): AuthResponseDto

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto)

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequestDto): VerifyOtpResponseDto

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto)

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto)

    @POST("auth/logout")
    suspend fun logout()

    @DELETE("auth/account")
    suspend fun deleteAccount()

    @Headers("${AuthInterceptor.HEADER_NO_AUTH}: true")
    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequestDto): AuthResponseDto
}
