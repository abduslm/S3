package com.my.kasirtemeji.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
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
/*
    // Views
    private lateinit var rvWifiList: RecyclerView
    private lateinit var pbLoading: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var etNamaWifi: EditText
    private lateinit var etPasswordWifi: EditText
    private lateinit var tvWifiId: TextView // Hidden ID untuk edit mode
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
        // RecyclerView dan Loading
        rvWifiList = view.findViewById(R.id.rv_wifi_list)
        pbLoading = view.findViewById(R.id.pb_loading)
        tvEmptyState = view.findViewById(R.id.tv_empty_state)

        // Form Input
        etNamaWifi = view.findViewById(R.id.et_nama_wifi)
        etPasswordWifi = view.findViewById(R.id.et_password_wifi)
        tvWifiId = view.findViewById(R.id.tv_wifi_id)

        // Buttons
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
            onItemClick = { wifi ->
                // Edit mode ketika item diklik
                setEditMode(wifi)
            },
            onDeleteClick = { wifi ->
                // Hapus WiFi
                deleteWifi(wifi.id)
            }
        )

        rvWifiList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = wifiAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupListeners() {
        // Reset Button
        btnReset.setOnClickListener {
            resetForm()
        }

        // Simpan Button
        btnSimpan.setOnClickListener {
            if (validateForm()) {
                if (isEditMode) {
                    updateWifi()
                } else {
                    addWifi()
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        val namaWifi = etNamaWifi.text.toString().trim()
        val password = etPasswordWifi.text.toString().trim()

        // Validasi Nama WiFi
        if (namaWifi.isEmpty()) {
            etNamaWifi.error = "Nama WiFi tidak boleh kosong"
            etNamaWifi.requestFocus()
            return false
        }

        // Validasi Password
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

        // Reset button text
        btnSimpan.text = "Simpan"

        // Clear errors
        etNamaWifi.error = null
        etPasswordWifi.error = null

        Toast.makeText(requireContext(), "Form telah direset", Toast.LENGTH_SHORT).show()
    }

    private fun setEditMode(wifi: Wifi) {
        isEditMode = true
        currentWifiId = wifi.id

        // Isi form dengan data WiFi
        etNamaWifi.setText(wifi.namaWifi)
        etPasswordWifi.setText(wifi.password)
        tvWifiId.text = wifi.id.toString()

        // Ubah text button
        btnSimpan.text = "Update"

        // Scroll ke form
        etNamaWifi.requestFocus()

        Toast.makeText(requireContext(), "Edit mode: ${wifi.namaWifi}", Toast.LENGTH_SHORT).show()
    }

    private fun loadWifiData() {
        showLoading(true)

        lifecycleScope.launch {
            // Ganti ini dengan fungsi yang benar dari ApiRepository
            // Anda perlu membuat fungsi getWifiList() yang mengembalikan ApiResult<List<Wifi>>
            val result = apiRepository.getWifiList()

            // Handle result sesuai dengan ApiResult
            when (result) {
                is ApiResult.Success -> {
                    val data = result.data // Ini akan berisi List<Wifi>
                    wifiList.clear()
                    data?.let { list ->
                        wifiList.addAll(list)
                    }

                    wifiAdapter.notifyDataSetChanged()

                    if (wifiList.isEmpty()) {
                        showEmptyState(true)
                    } else {
                        showEmptyState(false)
                    }
                }
                is ApiResult.Error -> {
                    showError(result.message ?: "Gagal memuat data WiFi")
                    showEmptyState(true)
                }
            }

            showLoading(false)
        }
    }

    private fun addWifi() {
        val namaWifi = etNamaWifi.text.toString().trim()
        val password = etPasswordWifi.text.toString().trim()

        showLoading(true)

        lifecycleScope.launch {
            // Ganti ini dengan fungsi yang benar dari ApiRepository
            val result = apiRepository.addWifi(namaWifi, password)

            showLoading(false)

            when (result) {
                is ApiResult.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "WiFi berhasil ditambahkan",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Reset form dan reload data
                    resetForm()
                    loadWifiData()
                }
                is ApiResult.Error -> {
                    showError(result.message ?: "Gagal menambahkan WiFi")
                }
            }
        }
    }

    private fun updateWifi() {
        val namaWifi = etNamaWifi.text.toString().trim()
        val password = etPasswordWifi.text.toString().trim()

        showLoading(true)

        lifecycleScope.launch {
            // Ganti ini dengan fungsi yang benar dari ApiRepository
            val result = apiRepository.updateWifi(currentWifiId, namaWifi, password)

            showLoading(false)

            when (result) {
                is ApiResult.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "WiFi berhasil diupdate",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Reset form dan reload data
                    resetForm()
                    loadWifiData()
                }
                is ApiResult.Error -> {
                    showError(result.message ?: "Gagal mengupdate WiFi")
                }
            }
        }
    }

    private fun deleteWifi(wifiId: Int) {
        // Show confirmation dialog
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
            // Ganti ini dengan fungsi yang benar dari ApiRepository
            val result = apiRepository.deleteWifi(wifiId)

            showLoading(false)

            when (result) {
                is ApiResult.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "WiFi berhasil dihapus",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Reload data
                    loadWifiData()

                    // Jika yang dihapus sedang di-edit, reset form
                    if (currentWifiId == wifiId) {
                        resetForm()
                    }
                }
                is ApiResult.Error -> {
                    showError(result.message ?: "Gagal menghapus WiFi")
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        pbLoading.visibility = if (show) View.VISIBLE else View.GONE
        btnSimpan.isEnabled = !show
        btnReset.isEnabled = !show
    }

    private fun showEmptyState(show: Boolean) {
        tvEmptyState.visibility = if (show) View.VISIBLE else View.GONE
        rvWifiList.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
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

            // Set click listener untuk edit (klik pada item)
            holder.itemView.setOnClickListener {
                onItemClick(wifi)
            }

            // Set click listener untuk delete
            holder.btnDelete.setOnClickListener {
                onDeleteClick(wifi)
            }
        }

        override fun getItemCount(): Int = wifiList.size

        fun updateData(newWifiList: List<Wifi>) {
            wifiList = newWifiList
            notifyDataSetChanged()
        }
    }

 */
}