package com.example.asramaku.data.session


object UserSession {

    var userId: Int? = null
    var userName: String? = null
    var email: String? = null

    fun isLoggedIn(): Boolean {
        return userId != null
    }

    fun clear() {
        userId = null
        userName = null
        email = null
    }
}
