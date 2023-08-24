package com.ieee.codelink.data.remote

import com.ieee.codelink.core.BaseResponse
import com.ieee.codelink.domain.models.AuthResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiAuthService {

    @POST(LOGIN_END_POINT)
    suspend fun loginUser(
        @Query("email")
        email: String? = null,
        @Query("password")
        password: String? = null
    ): Response<AuthResponse>

    @POST(SIGNUP_END_POINT)
    suspend fun signUpUser(
        @Query("name")
        name: String? = null,
        @Query("email")
        email: String? = null,
        @Query("password")
        password: String? = null,
        @Query("password_confirmation")
        password_confirmation: String? = null
    ): Response<BaseResponse>
}