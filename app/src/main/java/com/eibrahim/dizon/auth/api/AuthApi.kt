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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @POST("/api/Authentication/signup")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("/api/Authentication/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyRequest): Response<VerifyResponse>

    @POST("/api/Authentication/verify-otp")
    suspend fun verifyOtpForReset(@Body request: OtpRequest): Response<OtpResponse>

    @POST("/api/Authentication/resend-otp")
    suspend fun resendOtp(@Body request: ResendOtpRequest): Response<ResendOtpResponse>

    @POST("/api/Authentication/forgot-password")
    suspend fun forgotPassword(@Body request: ForgetPasswordRequest): Response<ForgetPasswordResponse>

    @POST("/api/Authentication/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>

    @POST("/api/Authentication/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/Authentication/login-google")
    suspend fun loginGoogleWithToken(@Body request: GoogleLoginRequest): Response<LoginResponse>

    @GET("/api/Authentication/google-response")
    suspend fun googleResponse(@Query("returnUrl") returnUrl: String = "/"): Response<LoginResponse>

    @GET("/api/User/GetUser")
    suspend fun getUser(): Response<UserResponse>

    @Multipart
    @PUT("/api/User/Update")
    suspend fun updateUser(
        @Part("FirstName") firstName: RequestBody,
        @Part("LastName") lastName: RequestBody,
        @Part("Email") email: RequestBody,
        @Part("PhoneNumber") phoneNumber: RequestBody,
        @Part("City") city: RequestBody,
        @Part("ImageUrl") imageUrl: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<UpdateResponse>

    @PUT("/api/User/ChangePassword")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ChangePasswordResponse>

    @POST("/api/User/Logout")
    suspend fun logout(): Response<LogoutResponse>
}

data class LogoutResponse(
    val status: String?,
    val message: String?
)

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val confirmNewPassword: String
)

data class ChangePasswordResponse(
    val status: String?,
    val message: String?
)

data class UserResponse(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val city: String?,
    val imageUrl: String?
)

data class UpdateResponse(
    val status: String?,
    val message: String?
)

data class GoogleLoginRequest(
    val idToken: String
)