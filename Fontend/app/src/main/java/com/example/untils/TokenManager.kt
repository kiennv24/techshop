package com.example.untils

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("AUTH_PREFS", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "JWT_TOKEN"
        private const val USER_ID_KEY = "USER_ID"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).commit()
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    // ✅ Đổi tham số từ Int sang Long
    fun saveUserId(userId: Long) {
        prefs.edit().putLong(USER_ID_KEY, userId).commit()
    }

    // ✅ Đổi kiểu trả về từ Int sang Long
    fun getUserId(): Long {
        return prefs.getLong(USER_ID_KEY, -1L)
    }

    fun clearAuth() {
        prefs.edit().clear().commit()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}