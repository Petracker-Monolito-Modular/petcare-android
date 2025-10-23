package com.example.petracker.feature_auth.data

import com.example.petracker.core.storage.TokenStore

class AuthRepository(
    private val api: AuthApi,
    private val tokenStore: TokenStore
) {
    suspend fun login(email: String, password: String): Result<Unit> = try {
        val token = api.login(LoginReq(email, password))
        tokenStore.saveToken(token.access_token)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}