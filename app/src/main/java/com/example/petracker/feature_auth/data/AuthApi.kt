package com.example.petracker.feature_auth.data

import com.example.petracker.common.model.TokenOut
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginReq(val email: String, val password: String)


data class RegisterReq(val email: String, val password: String, val name: String)

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body body: LoginReq): TokenOut


    @POST("auth/register")
    suspend fun register(@Body body: RegisterReq): UserOut
}


data class UserOut(val id: String, val email: String, val name: String)