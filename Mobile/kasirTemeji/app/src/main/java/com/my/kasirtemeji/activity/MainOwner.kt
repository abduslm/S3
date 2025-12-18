package com.my.kasirtemeji.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var mainContent: LinearLayout
    private lateinit var headerLayout: LinearLayout

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
        mainContent = findViewById(R.id.main_content)
        headerLayout = findViewById(R.id.header)

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
            //restoreDefaultHeader()
        }

        btnLihatUser.setOnClickListener {
            selectMenu(btnLihatUser)
            showFragment(LihatUserFragment(), "Lihat User")
            //setupLihatUserHeader()
        }

        btnLihatMenu.setOnClickListener {
            selectMenu(btnLihatMenu)
            showFragment(LihatMenuFragment(), "Lihat Menu")
            //setupLihatMenuHeader()
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

    private fun showFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, fragment, tag)
            .commit()
    }

    /*
    private fun setupLihatUserHeader() {
        // Hapus semua child kecuali logo dan app name
        while (headerLayout.childCount > 2) {
            headerLayout.removeViewAt(headerLayout.childCount - 1)
        }

        // Tambahkan search dan filter di header
        val lihatUserHeader = layoutInflater.inflate(R.layout.header_lihat_user, null)
        headerLayout.addView(lihatUserHeader)

        // Setup listeners untuk header lihat user
        setupLihatUserHeaderListeners(lihatUserHeader)
    }

    private fun setupLihatUserHeaderListeners(headerView: android.view.View) {
        val etSearch = headerView.findViewById<android.widget.EditText>(R.id.et_search)
        val btnFilterRole = headerView.findViewById<android.widget.TextView>(R.id.btn_filter_role)
        val btnFilterDate = headerView.findViewById<android.widget.TextView>(R.id.btn_filter_date)

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val searchText = etSearch.text.toString()
                (supportFragmentManager.findFragmentByTag("Lihat User") as? LihatUserFragment)?.searchUser(searchText)
                true
            } else {
                false
            }
        }

        btnFilterRole.setOnClickListener {
            showRoleFilterDialog()
        }

        btnFilterDate.setOnClickListener {
            showDateFilterDialog()
        }
    }

    private fun setupLihatMenuHeader() {
        // Hapus semua child kecuali logo dan app name
        while (headerLayout.childCount > 2) {
            headerLayout.removeViewAt(headerLayout.childCount - 1)
        }

        // Tambahkan header khusus lihat menu
        val lihatMenuHeader = layoutInflater.inflate(R.layout.header_lihat_menu, null)
        headerLayout.addView(lihatMenuHeader)

        // Setup listeners untuk header lihat menu
        setupLihatMenuHeaderListeners(lihatMenuHeader)
    }

    private fun setupLihatMenuHeaderListeners(headerView: android.view.View) {
        val etSearch = headerView.findViewById<android.widget.EditText>(R.id.et_search_menu)
        val btnFilterCategory = headerView.findViewById<android.widget.TextView>(R.id.btn_filter_category)

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val searchText = etSearch.text.toString()
                (supportFragmentManager.findFragmentByTag("Lihat Menu") as? LihatMenuOwnerFragment)?.searchMenu(searchText)
                true
            } else {
                false
            }
        }

        btnFilterCategory.setOnClickListener {
            showCategoryFilterDialog()
        }
    }

    private fun restoreDefaultHeader() {
        // Hapus semua child kecuali logo dan app name
        while (headerLayout.childCount > 2) {
            headerLayout.removeViewAt(headerLayout.childCount - 1)
        }

        // Tambahkan kembali welcome text
        val tvWelcome = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
                marginStart = 16
            }
            text = "Selamat datang, Owner ${sessionManager.getNama() ?: sessionManager.getUsername()}"
            textSize = 14f
            setTextColor(resources.getColor(R.color.teksColor, null))
        }

        headerLayout.addView(tvWelcome, headerLayout.childCount - 1)
    }
*/

    private fun showLogoutConfirmation() {
        if (SessionManager.getInstance()?.isLoggedIn()==true) {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Apakah Anda yakin ingin logout?")
                .setPositiveButton("Ya") { _, _ ->
                    doLogout()
                    Toast.makeText(this@MainOwner, "Logout berhasil", Toast.LENGTH_SHORT).show()
                    restartActivity()
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