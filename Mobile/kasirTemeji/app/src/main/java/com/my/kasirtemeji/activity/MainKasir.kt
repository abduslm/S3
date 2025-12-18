package com.my.kasirtemeji.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.my.kasirtemeji.MainActivity
import com.my.kasirtemeji.R
import com.my.kasirtemeji.api.ApiRepository
import com.my.kasirtemeji.api.RetrofitInstance
import com.my.kasirtemeji.util.NetworkUtils
import com.my.kasirtemeji.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainKasir : AppCompatActivity() {
    private lateinit var btnBack: Button

    private lateinit var apiRepository: ApiRepository
    private lateinit var networkUtils: NetworkUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_kasir)

        setupDependencies()
        initViews()

        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
    }
    private fun setupDependencies() {
        // Untuk cek koneksi internet
        networkUtils = NetworkUtils(this)

        // Setup repository untuk API
        val apiService = RetrofitInstance.apiService
        val sessionManager = SessionManager.getInstance()!!
        apiRepository = ApiRepository(apiService, networkUtils, sessionManager)
    }


    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        if (SessionManager.getInstance()?.isLoggedIn()==true) {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Apakah Anda yakin ingin logout?")
                .setPositiveButton("Ya") { _, _ ->
                    doLogout()
                    Toast.makeText(this@MainKasir, "Logout berhasil", Toast.LENGTH_SHORT).show()
                    restartActivity()
                }
                .setNegativeButton("Tidak", null)
                .show()
        } else {
            Toast.makeText(this@MainKasir, "Anda belum login", Toast.LENGTH_SHORT).show()
        }
    }

    private fun doLogout() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                apiRepository.logout()
            } catch (e: Exception) {
            } finally {
                // Clear session lokal
                SessionManager.getInstance()?.clearSession()
                Toast.makeText(this@MainKasir, "Logout berhasil", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun restartActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (SessionManager.getInstance()?.isLoggedIn()==true) {
            AlertDialog.Builder(this)
                .setTitle("Keluar Aplikasi")
                .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
                .setPositiveButton("Ya") { _, _ ->
                    finishAffinity()
                }
                .setNegativeButton("Tidak", null)
                .show()
        } else {
            super.onBackPressed()
        }
    }

}
