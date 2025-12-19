package com.my.kasirtemeji.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.my.kasirtemeji.R
import com.my.kasirtemeji.api.ApiRepository
import com.my.kasirtemeji.api.RetrofitInstance
import com.my.kasirtemeji.models.ApiResult
import com.my.kasirtemeji.models.UserItem
import com.my.kasirtemeji.util.NetworkUtils
import com.my.kasirtemeji.util.SessionManager
import kotlinx.coroutines.launch

class LihatUserFragment : Fragment() {

    private lateinit var tableLayout: TableLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var apiRepository: ApiRepository
    private lateinit var sessionManager: SessionManager

    companion object {
        fun newInstance() = LihatUserFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.lihatuser_owner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupApiRepository()
        setupListeners()
        loadUsersData()
    }

    private fun setupViews(view: View) {
        // Cari table layout
        tableLayout = view.findViewById(R.id.table_layout_user)

        // Buat progress bar programmatically jika tidak ada di XML
        progressBar = ProgressBar(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Tambah progress bar ke layout utama
        val mainContent = view.findViewById<LinearLayout>(R.id.mainContent)
        if (mainContent != null) {
            mainContent.addView(progressBar, 0)
        }

        // Buat empty state programmatically
        emptyState = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )

            val textView = TextView(requireContext()).apply {
                text = "Belum ada data user"
                textSize = 16f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                setPadding(0, 32.dpToPx(), 0, 0)
            }

            addView(textView)
        }

        if (mainContent != null) {
            mainContent.addView(emptyState)
        }

        // Sembunyikan dulu
        progressBar.visibility = View.GONE
        emptyState.visibility = View.GONE
    }

    private fun setupApiRepository() {
        sessionManager = SessionManager.getInstance()!!
        val networkUtils = NetworkUtils(requireContext())
        apiRepository = ApiRepository(
            RetrofitInstance.apiService,
            networkUtils,
            sessionManager
        )
    }

    private fun setupListeners() {
        // Tambah listener jika diperlukan (refresh, dll)
    }

    private fun loadUsersData() {
        showLoading(true)

        lifecycleScope.launch {
            val result = apiRepository.getAllUsers()

            if (result.status) {
                val users = result.data ?: emptyList()
                if (users.isEmpty()) {
                    showEmptyState(true)
                } else {
                    showEmptyState(false)
                    populateTable(users)
                }
            } else {
                showError(result.message ?: "Gagal memuat data user")
                showEmptyState(true)
            }

            showLoading(false)
        }
    }

    private fun populateTable(users: List<UserItem>) {
        // Clear existing rows except header
        while (tableLayout.childCount > 1) {
            tableLayout.removeViewAt(1)
        }

        users.forEach { user ->
            addTableRow(user)
        }
    }

    private fun addTableRow(user: UserItem) {
        val tableRow = TableRow(requireContext()).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

            // Add click listener untuk edit/action jika diperlukan
            setOnClickListener {
                // Navigasi ke edit user jika diperlukan
                // navigateToEditUser(user)
            }
        }

        // Nama Column
        tableRow.addView(createTableCell(user.nama ?: "-", 12f))

        // Username Column
        tableRow.addView(createTableCell(user.username, 12f))

        // Level Column
        val levelText = when (user.level.uppercase()) {
            "OWNER" -> "Owner"
            "SPV" -> "Supervisor"
            "KASIR" -> "Kasir"
            "DAPUR" -> "Dapur"
            else -> user.level
        }
        val levelCell = createTableCell(levelText, 12f)
        levelCell.setTextColor(getRoleColor(user.level))
        tableRow.addView(levelCell)

        // Email Column
        tableRow.addView(createTableCell(user.email ?: "-", 12f))

        // No. HP Column
        tableRow.addView(createTableCell(user.noHp ?: "-", 12f))

        // Status Column
        val statusCell = createTableCell(user.status ?: "OFFLINE", 12f)
        statusCell.setTextColor(getStatusColor(user.status ?: "OFFLINE"))
        tableRow.addView(statusCell)

        tableLayout.addView(tableRow)
    }

    private fun createTableCell(text: String, textSize: Float): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            this.textSize = textSize
            gravity = android.view.Gravity.CENTER
            setPadding(
                12.dpToPx(),
                16.dpToPx(),
                12.dpToPx(),
                16.dpToPx()
            )
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
    }

    private fun getRoleColor(role: String): Int {
        return when (role.uppercase()) {
            "OWNER" -> ContextCompat.getColor(requireContext(), R.color.owner_color)
            "SPV" -> ContextCompat.getColor(requireContext(), R.color.spv_color)
            "KASIR" -> ContextCompat.getColor(requireContext(), R.color.kasir_color)
            "DAPUR" -> ContextCompat.getColor(requireContext(), R.color.dapur_color)
            else -> ContextCompat.getColor(requireContext(), R.color.gray)
        }
    }

    private fun getStatusColor(status: String): Int {
        return when (status.uppercase()) {
            "ONLINE" -> ContextCompat.getColor(requireContext(), R.color.green)
            "OFFLINE" -> ContextCompat.getColor(requireContext(), R.color.red)
            else -> ContextCompat.getColor(requireContext(), R.color.gray)
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        tableLayout.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showEmptyState(show: Boolean) {
        emptyState.visibility = if (show) View.VISIBLE else View.GONE
        tableLayout.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
}