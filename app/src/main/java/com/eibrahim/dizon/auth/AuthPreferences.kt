package com.eibrahim.dizon.auth

import android.content.Context
import android.content.SharedPreferences

class AuthPreferences(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String?) {
        preferences.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        return preferences.getString("auth_token", null)
    }

    fun clearToken() {
        preferences.edit().remove("auth_token").apply()
    }

    fun isFirstLaunch(): Boolean {
        val isFirst = preferences.getBoolean("is_first_launch", true)
        if (isFirst) {
            preferences.edit().putBoolean("is_first_launch", false).apply()
        }
        return isFirst
    }
}