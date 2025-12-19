package com.my.kasirtemeji.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.my.kasirtemeji.MainActivity
import com.my.kasirtemeji.R
import com.my.kasirtemeji.api.ApiRepository
import com.my.kasirtemeji.api.RetrofitInstance
import com.my.kasirtemeji.fragment.DashboardFragment
import com.my.kasirtemeji.fragment.LihatMenuFragment
import com.my.kasirtemeji.fragment.LihatUserFragment
import com.my.kasirtemeji.util.NetworkUtils
import com.my.kasirtemeji.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainOwner : AppCompatActivity() {

    private lateinit var btnDashboard: TextView
    private lateinit var btnLihatUser: TextView
    private lateinit var btnLihatMenu: TextView
    private lateinit var btnBack: Button
    private lateinit var etCari: EditText
    private lateinit var mainContent: ConstraintLayout

    private lateinit var fKategori : LinearLayout
    private lateinit var fRole : LinearLayout
    private lateinit var fDate : LinearLayout

    private lateinit var apiRepository: ApiRepository
    private lateinit var networkUtils: NetworkUtils

    private var currentSelectedMenu: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_owner)

        setupDependencies()
        checkAuthentication()

        initViews()

        setupClickListeners()

        // Default: tampilkan dashboard
        selectMenu(btnDashboard)
        showFragment(DashboardFragment(), "Dashboard")
    }

    private fun checkAuthentication() {
        if (SessionManager.getInstance()?.isLoggedIn()==false) {
            val intent = android.content.Intent(this, com.my.kasirtemeji.MainActivity::class.java)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun initViews() {
        btnDashboard = findViewById(R.id.btn_dashboard)
        btnLihatUser = findViewById(R.id.btn_lihatuser)
        btnLihatMenu = findViewById(R.id.btn_lihatmenu)
        btnBack = findViewById(R.id.btn_back)
        etCari = findViewById(R.id.et_search)
        fKategori = findViewById(R.id.filter_kategori)
        fRole = findViewById(R.id.filter_role)
        fDate = findViewById(R.id.filter_date)
        mainContent = findViewById(R.id.main_content)


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
        btnDashboard.setOnClickListener {
            selectMenu(btnDashboard)
            showFragment(DashboardFragment(), "Dashboard")
            headerDashb()
        }

        btnLihatUser.setOnClickListener {
            selectMenu(btnLihatUser)
            showFragment(LihatUserFragment(), "Lihat User")
            headerLUser()
        }

        btnLihatMenu.setOnClickListener {
            selectMenu(btnLihatMenu)
            showFragment(LihatMenuFragment(), "Lihat Menu")
            headerLMenu()
        }

        btnBack.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun selectMenu(selectedMenu: TextView) {
        // Reset semua menu
        resetAllMenus()

        // Set menu terpilih
        selectedMenu.setTextColor(resources.getColor(R.color.sidebarAktifTeks, null))
        selectedMenu.background = resources.getDrawable(R.drawable.bg_sidebaraktifmenu, null)

        currentSelectedMenu = selectedMenu
    }

    private fun resetAllMenus() {
        btnDashboard.setTextColor(resources.getColor(R.color.sidebarTeks, null))
        btnDashboard.background = resources.getDrawable(R.drawable.bg_sidebarmenu, null)

        btnLihatUser.setTextColor(resources.getColor(R.color.sidebarTeks, null))
        btnLihatUser.background = resources.getDrawable(R.drawable.bg_sidebarmenu, null)

        btnLihatMenu.setTextColor(resources.getColor(R.color.sidebarTeks, null))
        btnLihatMenu.background = resources.getDrawable(R.drawable.bg_sidebarmenu, null)
    }

    private fun headerDashb(){
        etCari.text.clear()
        etCari.setVisibility(View.GONE)
        fKategori.setVisibility(View.GONE)
        fRole.setVisibility(View.GONE)
        fDate.setVisibility(View.GONE)
    }
    private fun headerLMenu(){
        etCari.text.clear()
        etCari.setVisibility(View.VISIBLE)
        fKategori.setVisibility(View.VISIBLE)
        fRole.setVisibility(View.GONE)
        fDate.setVisibility(View.GONE)
    }
    private fun headerLUser(){
        etCari.text.clear()
        etCari.setVisibility(View.VISIBLE)
        fKategori.setVisibility(View.GONE)
        fRole.setVisibility(View.VISIBLE)
        fDate.setVisibility(View.VISIBLE)
    }


    private fun showFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, fragment, tag)
            .commit()
    }

    private fun showLogoutConfirmation() {
        if (SessionManager.getInstance()?.isLoggedIn()==true) {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Apakah Anda yakin ingin logout?")
                .setPositiveButton("Ya") { _, _ ->
                    doLogout()
                }
                .setNegativeButton("Tidak", null)
                .show()
        } else {
            Toast.makeText(this@MainOwner, "Anda belum login", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@MainOwner, "Logout berhasil", Toast.LENGTH_SHORT).show()
                restartActivity()
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