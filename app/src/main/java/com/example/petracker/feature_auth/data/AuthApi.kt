package com.example.petracker.feature_auth.data

import com.example.petracker.common.model.TokenOut
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginReq(val email: String, val password: String)

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body body: LoginReq): TokenOut
}