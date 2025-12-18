package com.my.kasirtemeji.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.my.kasirtemeji.models.User

class SessionManager(context: Context) {

    companion object {
        private var instance: SessionManager? = null

        fun initialize(context: Context) {
            if (instance == null) {
                instance = SessionManager(context.applicationContext)
            }
        }

        fun getInstance(): SessionManager? {
            return instance
        }
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "KasirTemejiPrefs",
        Context.MODE_PRIVATE
    )

    private val gson = Gson()

    /**
     * Simpan JWT token
     */
    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    /**
     * Ambil token
     */
    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    /**
     * Simpan data user
     */
    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        prefs.edit().putString("user", userJson).apply()
    }

    /**
     * Ambil data user
     */
    fun getUser(): User? {
        val userJson = prefs.getString("user", null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }

    /**
     * Ambil level user
     */
    fun getUserLevel(): String? {
        return getUser()?.level
    }

    /**
     * Cek apakah user sudah login
     */
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
    /**
     * Cek apakah token masih valid
     */
    fun isTokenValid(): Boolean {
        val token = getToken()
        // Implementasi sederhana, bisa ditambahkan pengecekan expiry
        return !token.isNullOrEmpty()
    }

    /**
     * Hapus semua data session (logout)
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}