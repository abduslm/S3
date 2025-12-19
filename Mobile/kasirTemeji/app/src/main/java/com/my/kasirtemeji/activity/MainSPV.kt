package com.my.kasirtemeji.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.my.kasirtemeji.MainActivity
import com.my.kasirtemeji.R
import com.my.kasirtemeji.api.ApiRepository
import com.my.kasirtemeji.api.RetrofitInstance
import com.my.kasirtemeji.fragment.AddProdukFragment
import com.my.kasirtemeji.fragment.DashboardFragment
import com.my.kasirtemeji.fragment.KelolaMenuFragment
import com.my.kasirtemeji.fragment.KelolaWifiFragment
import com.my.kasirtemeji.models.MenuItem
import com.my.kasirtemeji.util.NetworkUtils
import com.my.kasirtemeji.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainSPV : AppCompatActivity() {

    private lateinit var btnDashboard: TextView
    private lateinit var btnKelolaMenu: TextView
    private lateinit var btnKelolaWifi: TextView
    private lateinit var btnBack: Button
    private lateinit var mainContent: ConstraintLayout
    private lateinit var etCari: EditText
    private lateinit var btnAddProduct: Button

    private lateinit var apiRepository: ApiRepository
    private lateinit var networkUtils: NetworkUtils

    private var currentSelectedMenu: TextView? = null
    private var isInAddProductMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_spv)

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
            // Redirect ke login
            val intent = android.content.Intent(this, com.my.kasirtemeji.MainActivity::class.java)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun initViews() {
        btnDashboard = findViewById(R.id.btn_dashboard)
        btnKelolaMenu = findViewById(R.id.btn_kelolamenu)
        btnKelolaWifi = findViewById(R.id.btn_kelolawifi)
        btnBack = findViewById(R.id.btn_back)
        mainContent = findViewById(R.id.main_content)
        //headerLayout = findViewById(R.id.header)
        etCari = findViewById(R.id.et_search)
        btnAddProduct = findViewById(R.id.btn_addproduct)

        etCari.setVisibility(View.GONE)
        btnAddProduct.setVisibility(View.GONE)
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
            isInAddProductMode = false
            selectMenu(btnDashboard)
            showFragment(DashboardFragment(), "Dashboard")
            //defaultHeader()
            etCari.text.clear()
            etCari.clearFocus()
            etCari.setVisibility(View.GONE)
            btnAddProduct.setVisibility(View.GONE)
        }

        btnKelolaMenu.setOnClickListener {
            isInAddProductMode = false
            selectMenu(btnKelolaMenu)
            showFragment(KelolaMenuFragment(), "Kelola Menu")
            //setupKelolaMenuHeader()
            etCari.text.clear()
            etCari.setVisibility(View.VISIBLE)
            btnAddProduct.setVisibility(View.VISIBLE)
        }

        btnKelolaWifi.setOnClickListener {
            isInAddProductMode = false
            selectMenu(btnKelolaWifi)
            showFragment(KelolaWifiFragment(), "Kelola Wifi")
            //defaultHeader()
            etCari.text.clear()
            etCari.setVisibility(View.VISIBLE)
            btnAddProduct.setVisibility(View.GONE)
        }

        btnAddProduct.setOnClickListener {
            if (isInAddProductMode) {
                // Kembali ke daftar menu
                isInAddProductMode = false
                showFragment(KelolaMenuFragment(), "Kelola Menu")
                btnAddProduct.text = "+ Add Product"
                // Refresh data
                refreshKelolaMenu()
            } else {
                // Buka form add product
                isInAddProductMode = true
                showFragment(AddProdukFragment(), "Add Produk")
                btnAddProduct.text = "← Kembali"
            }
        }

        btnBack.setOnClickListener {
            showLogoutConfirmation()
        }

    }

    private fun refreshKelolaMenu() {
        val fragment = supportFragmentManager.findFragmentByTag("Kelola Menu")
        if (fragment is KelolaMenuFragment && fragment.isVisible) {
            // Trigger reload data
            fragment.loadMenuData()
        }
    }

    fun navigateToAddProduct(menu: MenuItem? = null) {
        val fragment = if (menu != null) {
            showFragment(AddProdukFragment.newInstance(menu), "Edit Produk")
        } else {
            showFragment(AddProdukFragment(), "Add Produk")
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

        btnKelolaMenu.setTextColor(resources.getColor(R.color.sidebarTeks, null))
        btnKelolaMenu.background = resources.getDrawable(R.drawable.bg_sidebarmenu, null)

        btnKelolaWifi.setTextColor(resources.getColor(R.color.sidebarTeks, null))
        btnKelolaWifi.background = resources.getDrawable(R.drawable.bg_sidebarmenu, null)
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, fragment, tag)
            .commit()
    }


    override fun onBackPressed() {
        if (isInAddProductMode) {
            // Kembali ke daftar menu
            isInAddProductMode = false
            showFragment(KelolaMenuFragment(), "Kelola Menu")
            btnAddProduct.text = "+ Add Product"
        } else {
            // Tampilkan exit confirmation
            showExitAplikasi()
        }
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
            Toast.makeText(this@MainSPV, "Anda belum login", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@MainSPV, "Logout berhasil", Toast.LENGTH_SHORT).show()
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


    private fun showExitAplikasi() {
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
