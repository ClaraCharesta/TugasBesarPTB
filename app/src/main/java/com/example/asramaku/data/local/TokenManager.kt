package com.example.asramaku.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// ----------------------
//   DATASTORE INSTANCE
// ----------------------
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token_key")
        private val USER_ID = intPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
    }

    // -------------------------------------------------
    //  SAVE SESSION → ketika login berhasil
    // -------------------------------------------------
    suspend fun saveSession(token: String, userId: Int, userName: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID] = userId
            prefs[USER_NAME] = userName
        }
    }

    // -------------------------------------------------
    //  READ SESSION (Flow)
    // -------------------------------------------------
    val token: Flow<String> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) // <-- mencegah crash
            else throw e
        }
        .map { it[TOKEN_KEY] ?: "" }

    val userId: Flow<Int> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
        .map { it[USER_ID] ?: 0 }

    val userName: Flow<String> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
        .map { it[USER_NAME] ?: "" }

    // -------------------------------------------------
    //  CLEAR SESSION → untuk logout
    // -------------------------------------------------
    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}



