package com.my.kasirtemeji

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.my.kasirtemeji.activity.*
import com.my.kasirtemeji.api.ApiRepository
import com.my.kasirtemeji.api.RetrofitInstance
import com.my.kasirtemeji.models.ApiResult
import com.my.kasirtemeji.models.LoginData
import com.my.kasirtemeji.models.LoginRequest
import com.my.kasirtemeji.util.NetworkUtils
import com.my.kasirtemeji.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    // Deklarasi komponen UI
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvError: TextView
    private lateinit var btnLogout: ImageButton

    // Untuk komunikasi dengan API
    private lateinit var apiRepository: ApiRepository
    private lateinit var networkUtils: NetworkUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_login)

        SessionManager.initialize(this)

        setupViews()
        setupDependencies()
        checkIfAlreadyLoggedIn()
    }

    /**
     * Menghubungkan komponen UI dari XML ke Kotlin
     */
    private fun setupViews() {
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_sign_in)
        tvError = findViewById(R.id.tv_error)
        btnLogout = findViewById(R.id.icon_logout)

        btnLogout.setOnClickListener {
            showExitDialog()
        }

        // Setup klik listener untuk tombol login
        btnLogin.setOnClickListener {
            onLoginButtonClicked()
        }


        // Setup untuk tombol "Done" di keyboard
        etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                onLoginButtonClicked()
                true
            } else {
                false
            }
        }
    }

    /**
     * Setup dependencies yang diperlukan
     */
    private fun setupDependencies() {
        // Untuk cek koneksi internet
        networkUtils = NetworkUtils(this)

        // Setup repository untuk API
        val apiService = RetrofitInstance.apiService
        val sessionManager = SessionManager.getInstance()!!
        apiRepository = ApiRepository(apiService, networkUtils, sessionManager)
    }

    /**
     * Cek apakah user sudah login sebelumnya
     */
    private fun checkIfAlreadyLoggedIn() {
        val sessionManager = SessionManager.getInstance()

        // Jika sudah login dan token masih valid
        if (sessionManager?.isLoggedIn() == true && sessionManager.isTokenValid()) {
            // Langsung redirect ke halaman sesuai role
            goToRolePage()
        }
    }

    /**
     * Dijalankan ketika tombol login ditekan
     */
    private fun onLoginButtonClicked() {
        // Sembunyikan keyboard
        hideKeyboard()

        // Ambil input dari user
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validasi input
        if (!isInputValid(username, password)) {
            return
        }

        // Cek koneksi internet
        if (!networkUtils.isNetworkAvailable()) {
            showError("Tidak ada koneksi internet")
            return
        }

        // Tampilkan loading
        showLoading(true)

        // Jalankan proses login di background thread
        CoroutineScope(Dispatchers.Main).launch {
            doLogin(username, password)
        }
    }

    /**
     * Validasi input username dan password
     */
    private fun isInputValid(username: String, password: String): Boolean {
        // Reset error
        clearError()

        // Cek username
        if (username.isEmpty()) {
            showError("Username harus diisi")
            etUsername.requestFocus()
            return false
        }

        // Cek password
        if (password.isEmpty()) {
            showError("Password harus diisi")
            etPassword.requestFocus()
            return false
        }

        return true
    }

    /**
     * Proses login ke server
     */
    private suspend fun doLogin(username: String, password: String) {
        try {
            // Buat request object
            val loginRequest = LoginRequest(username, password)

            // Panggil API
            val result = apiRepository.login(loginRequest)

            // Handle hasil dari API
            handleLoginResult(result)
        } catch (e: Exception) {
            // Tangani error
            showLoading(false)
            showError("Error: ${e.message}")
        }
    }

    /**
     * Menangani hasil dari API login
     */
    private fun handleLoginResult(result: ApiResult<LoginData>) {
        showLoading(false)

        // Cek apakah login berhasil
        if (result.status) {
            // Login berhasil
            result.data?.let { loginData ->
                showSuccessMessage()
                goToRolePage()
            } ?: run {
                // Data null padahal status success
                showError("Terjadi kesalahan, coba lagi")
            }
        } else {
            // Login gagal
            val errorMessage = result.message ?: "Login gagal"
            showError(errorMessage)

            // Clear password untuk keamanan
            etPassword.text?.clear()
            etPassword.requestFocus()
        }
    }

    /**
     * Pindah ke halaman sesuai role user
     */
    private fun goToRolePage() {
        val sessionManager = SessionManager.getInstance()
        val userLevel = sessionManager?.getUserLevel()

        // Tentukan halaman tujuan berdasarkan role
        val destination = when (userLevel?.uppercase()) {
            "OWNER" -> MainOwner::class.java
            "SPV" -> MainSPV::class.java
            "KASIR" -> MainKasir::class.java
            "DAPUR" -> MainDapur::class.java
            else -> {
                // Role tidak dikenali, tetap di login
                showError("Role tidak dikenali")
                return
            }
        }

        // Buat intent untuk pindah halaman
        val intent = Intent(this, destination)

        // Clear back stack agar tidak bisa kembali ke login
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Pindah halaman
        startActivity(intent)
        finish()
    }

    /**
     * Tampilkan loading state
     */
    private fun showLoading(show: Boolean) {
        // Nonaktifkan tombol dan input saat loading
        btnLogin.isEnabled = !show
        etUsername.isEnabled = !show
        etPassword.isEnabled = !show

        // Ubah text tombol
        btnLogin.text = if (show) "Loading..." else "MASUK"

        // Sembunyikan error jika loading
        if (show) {
            clearError()
        }
    }

    /**
     * Tampilkan pesan error
     */
    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    /**
     * Sembunyikan pesan error
     */
    private fun clearError() {
        tvError.visibility = View.GONE
        tvError.text = ""
    }

    /**
     * Tampilkan pesan sukses
     */
    private fun showSuccessMessage() {
        Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Sembunyikan keyboard
     */
    private fun hideKeyboard() {
        val view = currentFocus
        view?.let {
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    /**
     * Handle tombol back
     */
    override fun onBackPressed() {
            showExitDialog()
    }


    /**
     * Dialog konfirmasi keluar aplikasi
     */
    private fun showExitDialog() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Keluar Aplikasi")
            .setMessage("Yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                finishAffinity() // Tutup semua activity
            }
            .setNegativeButton("Tidak", null)
            .show()
    }


}
