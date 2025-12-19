package com.my.kasirtemeji.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.my.kasirtemeji.R
import com.my.kasirtemeji.api.ApiRepository
import com.my.kasirtemeji.api.RetrofitInstance
import com.my.kasirtemeji.models.ApiResult
import com.my.kasirtemeji.models.Wifi
import com.my.kasirtemeji.util.NetworkUtils
import com.my.kasirtemeji.util.SessionManager
import kotlinx.coroutines.launch

class KelolaWifiFragment : Fragment() {

    // Views
    private lateinit var rvWifiList: RecyclerView
    private lateinit var pbLoading: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var etNamaWifi: EditText
    private lateinit var etPasswordWifi: EditText
    private lateinit var tvWifiId: TextView
    private lateinit var btnReset: Button
    private lateinit var btnSimpan: Button

    // Adapter
    private lateinit var wifiAdapter: WifiAdapter

    // Repository
    private lateinit var apiRepository: ApiRepository

    // Data
    private val wifiList = mutableListOf<Wifi>()
    private var isEditMode = false
    private var currentWifiId: Int = 0

    companion object {
        fun newInstance() = KelolaWifiFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.kelolawifi_spv, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupApiRepository()
        setupRecyclerView()
        setupListeners()
        loadWifiData()
    }

    private fun setupViews(view: View) {
        rvWifiList = view.findViewById(R.id.rv_wifi_list)
        pbLoading = view.findViewById(R.id.pb_loading)
        tvEmptyState = view.findViewById(R.id.tv_empty_state)
        etNamaWifi = view.findViewById(R.id.et_nama_wifi)
        etPasswordWifi = view.findViewById(R.id.et_password_wifi)
        tvWifiId = view.findViewById(R.id.tv_wifi_id)
        btnReset = view.findViewById(R.id.btn_reset)
        btnSimpan = view.findViewById(R.id.btn_simpan)
    }

    private fun setupApiRepository() {
        val sessionManager = SessionManager.getInstance()
        val networkUtils = NetworkUtils(requireContext())
        apiRepository = ApiRepository(
            RetrofitInstance.apiService,
            networkUtils,
            sessionManager
        )
    }

    private fun setupRecyclerView() {
        wifiAdapter = WifiAdapter(
            wifiList = wifiList,
            onItemClick = { wifi -> setEditMode(wifi) },
            onDeleteClick = { wifi -> deleteWifi(wifi.id) }
        )

        rvWifiList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = wifiAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupListeners() {
        btnReset.setOnClickListener { resetForm() }
        btnSimpan.setOnClickListener {
            if (validateForm()) {
                if (isEditMode) updateWifi() else addWifi()
            }
        }
    }

    private fun validateForm(): Boolean {
        val namaWifi = etNamaWifi.text.toString().trim()
        val password = etPasswordWifi.text.toString().trim()

        if (namaWifi.isEmpty()) {
            etNamaWifi.error = "Nama WiFi tidak boleh kosong"
            etNamaWifi.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            etPasswordWifi.error = "Password tidak boleh kosong"
            etPasswordWifi.requestFocus()
            return false
        }

        if (password.length < 8) {
            etPasswordWifi.error = "Password minimal 8 karakter"
            etPasswordWifi.requestFocus()
            return false
        }

        return true
    }

    private fun resetForm() {
        etNamaWifi.text.clear()
        etPasswordWifi.text.clear()
        tvWifiId.text = ""

        isEditMode = false
        currentWifiId = 0
        btnSimpan.text = "Simpan"

        etNamaWifi.error = null
        etPasswordWifi.error = null

        showToast("Form telah direset")
    }

    private fun setEditMode(wifi: Wifi) {
        isEditMode = true
        currentWifiId = wifi.id

        etNamaWifi.setText(wifi.namaWifi)
        etPasswordWifi.setText(wifi.password)
        tvWifiId.text = wifi.id.toString()
        btnSimpan.text = "Update"

        etNamaWifi.requestFocus()
        showToast("Edit mode: ${wifi.namaWifi}")
    }

    private fun loadWifiData() {
        showLoading(true)

        lifecycleScope.launch {
            val result = apiRepository.getWifiList()

            // Cek status
            if (result.status) { // result.status == true
                val data = result.data ?: emptyList()
                wifiList.clear()
                wifiList.addAll(data)
                wifiAdapter.notifyDataSetChanged()

                if (wifiList.isEmpty()) {
                    showLoading(true)
                } else {
                    showLoading(false)
                }
            } else {
                // Error
                showError(result.message ?: "Gagal memuat data WiFi")
                showLoading(true)
            }

            showLoading(false)
        }
    }

    private fun addWifi() {
        val namaWifi = etNamaWifi.text.toString().trim()
        val password = etPasswordWifi.text.toString().trim()

        // Validasi sebelum lanjut
        if (!validateForm()) {
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            val result = apiRepository.addWifi(namaWifi, password)

            // Cek status result
            if (result.status) { // Jika sukses
                showToast("WiFi berhasil ditambahkan")
                resetForm()
                loadWifiData() // Reload data dari server
            } else { // Jika error
                showError(result.message ?: "Gagal menambahkan WiFi")
            }

            showLoading(false)
        }
    }

    private fun updateWifi() {
        val namaWifi = etNamaWifi.text.toString().trim()
        val password = etPasswordWifi.text.toString().trim()

        // Validasi sebelum lanjut
        if (!validateForm()) {
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            val result = apiRepository.updateWifi(currentWifiId, namaWifi, password)

            // Cek status result
            if (result.status) { // Jika sukses
                showToast("WiFi berhasil diupdate")
                resetForm()
                loadWifiData() // Reload data dari server
            } else { // Jika error
                showError(result.message ?: "Gagal mengupdate WiFi")
            }

            showLoading(false)
        }
    }

    private fun deleteWifi(wifiId: Int) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Hapus WiFi")
            .setMessage("Apakah Anda yakin ingin menghapus WiFi ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                performDeleteWifi(wifiId)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performDeleteWifi(wifiId: Int) {
        showLoading(true)

        lifecycleScope.launch {
            val result = apiRepository.deleteWifi(wifiId)

            // Cek status result
            if (result.status) { // Jika sukses
                showToast("WiFi berhasil dihapus")
                loadWifiData() // Reload data dari server

                // Jika yang dihapus sedang di-edit, reset form
                if (currentWifiId == wifiId) {
                    resetForm()
                }
            } else { // Jika error
                showError(result.message ?: "Gagal menghapus WiFi")
            }

            showLoading(false)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun showLoading(show: Boolean) {
        pbLoading.visibility = if (show) View.VISIBLE else View.GONE

        // Disable/Enable buttons saat loading
        btnSimpan.isEnabled = !show
        btnReset.isEnabled = !show

        // Disable/Enable form input
        etNamaWifi.isEnabled = !show
        etPasswordWifi.isEnabled = !show
    }



    // WifiAdapter class
    inner class WifiAdapter(
        private var wifiList: List<Wifi>,
        private val onItemClick: (Wifi) -> Unit,
        private val onDeleteClick: (Wifi) -> Unit
    ) : RecyclerView.Adapter<WifiAdapter.WifiViewHolder>() {

        inner class WifiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val wifiName: TextView = itemView.findViewById(R.id.wifi_name)
            val wifiUsername: TextView = itemView.findViewById(R.id.wifi_username)
            val btnDelete: TextView = itemView.findViewById(R.id.btn_delete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_wifi_list, parent, false)
            return WifiViewHolder(view)
        }

        override fun onBindViewHolder(holder: WifiViewHolder, position: Int) {
            val wifi = wifiList[position]

            holder.wifiName.text = wifi.namaWifi
            holder.wifiUsername.text = "Password: ${wifi.password}"

            holder.itemView.setOnClickListener { onItemClick(wifi) }
            holder.btnDelete.setOnClickListener { onDeleteClick(wifi) }
        }

        override fun getItemCount(): Int = wifiList.size

        fun updateData(newWifiList: List<Wifi>) {
            wifiList = newWifiList
            notifyDataSetChanged()
        }
    }
}