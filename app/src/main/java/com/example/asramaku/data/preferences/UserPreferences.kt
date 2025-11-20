package com.example.asramaku.data.preferences

import android.content.Context

class UserPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveSession(token: String, name: String, email: String) {
        prefs.edit().apply {
            putString("token", token)
            putString("name", name)
            putString("email", email)
            apply()
        }
    }

    fun getName(): String? = prefs.getString("name", null)
    fun getEmail(): String? = prefs.getString("email", null)
    fun getToken(): String? = prefs.getString("token", null)

    fun clear() {
        prefs.edit().clear().apply()
    }
}