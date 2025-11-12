package com.example.asramaku.model


import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(name: String, email: String, password: String) {
        prefs.edit()
            .putString("name", name)
            .putString("email", email)
            .putString("password", password)
            .apply()
    }

    fun getUser(): Triple<String?, String?, String?> {
        return Triple(
            prefs.getString("name", null),
            prefs.getString("email", null),
            prefs.getString("password", null)
        )
    }

    fun clearUser() {
        prefs.edit().clear().apply()
    }
}
