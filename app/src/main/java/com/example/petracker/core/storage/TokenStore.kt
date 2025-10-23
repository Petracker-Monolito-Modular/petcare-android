package com.example.petracker.core.storage

import android.content.Context

class TokenStore(context: Context) {
    private val sp = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        sp.edit().putString("access_token", token).apply()
    }
    fun getToken(): String? = sp.getString("access_token", null)
    fun clear() { sp.edit().remove("access_token").apply() }
}