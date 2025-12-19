package com.example.asramaku.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

// DATASTORE INSTANCE
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token_key")
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
    }

    // ================= SAVE SESSION =================
    suspend fun saveSession(
        token: String,
        userId: Int,
        userName: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId
            prefs[USER_NAME_KEY] = userName
        }
    }

    // ================= READ TOKEN =================
    val token: Flow<String> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
        .map { it[TOKEN_KEY] ?: "" }

    // ================= READ USER ID =================
    val userId: Flow<Int> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
        .map { it[USER_ID_KEY] ?: 0 }

    // ================= READ USER NAME =================
    val userName: Flow<String> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
        .map { it[USER_NAME_KEY] ?: "" }

    // ================= HELPER =================
    suspend fun getToken(): String {
        return token.first()
    }

    // ================= LOGOUT =================
    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}