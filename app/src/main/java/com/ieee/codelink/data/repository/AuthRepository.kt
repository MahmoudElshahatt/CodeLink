package com.ieee.codelink.data.repository

import com.ieee.codelink.data.local.preference.SharedPreferenceManger
import com.ieee.codelink.data.remote.ApiAuthService
import com.ieee.codelink.domain.models.AuthResponse
import com.ieee.codelink.domain.models.User
import retrofit2.Response

class AuthRepository(
    private val api: ApiAuthService,
    private val sharedPreferenceManger: SharedPreferenceManger
) {
    suspend fun loginUser(email: String, password: String): Response<AuthResponse>? {
        return try {
            api.loginUser(email, password)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun signup(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) = api.signUpUser(name, email, password, confirmPassword)

    suspend fun cacheUser(user: User) {
        sharedPreferenceManger.cacheUser(user)
    }


}