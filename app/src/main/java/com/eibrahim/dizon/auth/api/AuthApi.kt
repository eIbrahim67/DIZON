package com.eibrahim.dizon.auth.api

import com.eibrahim.dizon.auth.forgetpassword.viewModel.ForgetPasswordRequest
import com.eibrahim.dizon.auth.forgetpassword.viewModel.ForgetPasswordResponse
import com.eibrahim.dizon.auth.login.viewModel.LoginRequest
import com.eibrahim.dizon.auth.login.viewModel.LoginResponse
import com.eibrahim.dizon.auth.register.viewModel.RegisterRequest
import com.eibrahim.dizon.auth.register.viewModel.RegisterResponse
import com.eibrahim.dizon.auth.verify.viewModel.VerifyRequest
import com.eibrahim.dizon.auth.verify.viewModel.VerifyResponse
import com.eibrahim.dizon.auth.otp.viewModel.OtpRequest
import com.eibrahim.dizon.auth.otp.viewModel.OtpResponse
import com.eibrahim.dizon.auth.otp.viewModel.ResendOtpRequest
import com.eibrahim.dizon.auth.otp.viewModel.ResendOtpResponse
import com.eibrahim.dizon.auth.resetpassword.viewmodel.ResetPasswordRequest
import com.eibrahim.dizon.auth.resetpassword.viewmodel.ResetPasswordResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/Authorization/signup")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/Authorization/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyRequest): Response<VerifyResponse>

    @POST("api/Authorization/verify-otp")
    suspend fun verifyOtpForReset(@Body request: OtpRequest): Response<OtpResponse>

    @POST("api/Authorization/resend-otp")
    suspend fun resendOtp(@Body request: ResendOtpRequest): Response<ResendOtpResponse>

    @POST("api/Authorization/forgot-password")
    suspend fun forgotPassword(@Body request: ForgetPasswordRequest): Response<ForgetPasswordResponse>

    @POST("api/Authorization/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>

    @POST("/api/Authorization/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}