package com.my.kasirtemeji.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
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
import com.my.kasirtemeji.models.MenuItem
import com.my.kasirtemeji.util.NetworkUtils
import com.my.kasirtemeji.util.SessionManager
import kotlinx.coroutines.launch
import java.text.NumberFormat
import com.my.kasirtemeji.activity.MainSPV
import java.util.*

class KelolaMenuFragment : Fragment() {

    private lateinit var tableLayout: TableLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var btnTambahMenu: Button
    private lateinit var apiRepository: ApiRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.kelolamenu_spv, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupApiRepository()
        loadMenuData()
        setupListeners()
    }

    private fun setupViews(view: View) {
        tableLayout = view.findViewById(R.id.tabelmenu)
        progressBar = view.findViewById(R.id.progress_bar)
        emptyState = view.findViewById(R.id.empty_state)
        btnTambahMenu = requireActivity().findViewById(R.id.btn_addproduct)

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


    private fun setupListeners() {

    }


    internal fun loadMenuData(kategori: String? = null, status: String? = null) {
        showLoading(true)

        lifecycleScope.launch {
            val result = if (kategori != null || status != null) {
                apiRepository.getMenusWithFilter(com.my.kasirtemeji.models.MenuFilter(kategori, status))
            } else {
                apiRepository.getAllMenus()
            }

            hideLoading()

            if (result.status) {
                val menus = result.data ?: emptyList()
                if (menus.isEmpty()) {
                    showEmptyState()
                } else {
                    hideEmptyState()
                    populateTable(menus)
                }
            } else {
                showError(result.message ?: "Gagal memuat data")
            }
        }
    }

    private fun populateTable(menus: List<MenuItem>) {
        // Clear existing rows (keep header row)
        val childCount = tableLayout.childCount
        for (i in 1 until childCount) {
            tableLayout.removeViewAt(1)
        }

        // Add menu rows
        menus.forEach { menu ->
            addTableRow(menu)
        }
    }

    private fun addTableRow(menu: MenuItem) {
        val tableRow = TableRow(requireContext()).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT
            )
            background = ContextCompat.getDrawable(requireContext(), R.drawable.headertabel)
        }

        // Product Column with Image
        val productCell = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 150, 1f)
            setPadding(8, 4, 8, 4)
            background = ContextCompat.getDrawable(requireContext(), R.drawable.headertabel)
        }

        // ImageView for product
        val imageView = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(75, 75)
            scaleType = ImageView.ScaleType.CENTER_CROP
            setPadding(4, 4, 8, 4)
        }

        // Set image if available
        if (!menu.gambarBase64.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(menu.gambarBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                imageView.setImageResource(R.drawable.ic_sampah)
            }
        } else {
            imageView.setImageResource(R.drawable.ic_sampah)
        }

        val productName = TextView(requireContext()).apply {
            text = menu.namaMenu
            textSize = 12f
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
        }

        productCell.addView(imageView)
        productCell.addView(productName)

        // Category Column
        val categoryCell = createTableCell(menu.kategori,150)
        categoryCell.setBackgroundResource(R.drawable.headertabel)

        // Status Column
        val statusCell = createTableCell(menu.status, 150).apply {
            val color = when (menu.status.uppercase()) {
                "TERSEDIA" -> R.color.green
                "HABIS" -> R.color.red
                else -> android.R.color.black
            }
            setTextColor(ContextCompat.getColor(requireContext(), color))
        }
        statusCell.setBackgroundResource(R.drawable.headertabel)

        // Product ID Column
        val idCell = createTableCell("M${menu.id.toString().padStart(3, '0')}", 150)
        idCell.setBackgroundResource(R.drawable.headertabel)
        // Quantity Column
        val quantityCell = createTableCell(menu.stok.toString(), 150)
        quantityCell.setBackgroundResource(R.drawable.headertabel)

        // Price Column
        val priceFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        priceFormat.maximumFractionDigits = 0
        val priceCell = createTableCell(priceFormat.format(menu.harga), 150)
        priceCell.setBackgroundResource(R.drawable.headertabel)

        // Action Column
        val actionCell = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 150)
            setPadding(8, 4, 8, 4)
            background = ContextCompat.getDrawable(requireContext(), R.drawable.headertabel)
        }

        // Edit Button
        val editButton = TextView(requireContext()).apply {
            text = "Edit"
            textSize = 12f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_sampah, 0, 0, 0
            )
            compoundDrawablePadding = 4
            setPadding(0, 0, 8, 0)
            setOnClickListener {
                (requireActivity() as MainSPV).navigateToAddProduct(menu)
            }
        }

        // Delete Button (only for Owner/SPV)
        val deleteButton = TextView(requireContext()).apply {
            text = "Delete"
            textSize = 12f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_sampah, 0, 0, 0
            )
            compoundDrawablePadding = 4
            isEnabled = apiRepository.hasMenuDeletePermission()
            alpha = if (isEnabled) 1f else 0.5f
            setOnClickListener {
                if (apiRepository.hasMenuDeletePermission()) {
                    showDeleteConfirmation(menu)
                }
            }
        }

        actionCell.addView(editButton)
        actionCell.addView(deleteButton)

        // Add all cells to row
        tableRow.addView(productCell)
        tableRow.addView(categoryCell)
        tableRow.addView(statusCell)
        tableRow.addView(idCell)
        tableRow.addView(quantityCell)
        tableRow.addView(priceCell)
        tableRow.addView(actionCell)

        // Add row to table
        tableLayout.addView(tableRow)
    }

    private fun createTableCell(text: String, height: Int): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 12f
            gravity = android.view.Gravity.CENTER
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, height)
            setPadding(8, 4, 8, 4)
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
        }
    }

    private fun showDeleteConfirmation(menu: MenuItem) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Hapus Menu")
            .setMessage("Apakah Anda yakin ingin menghapus ${menu.namaMenu}?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deleteMenu(menu.id)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteMenu(menuId: Int) {
        showLoading(true)

        lifecycleScope.launch {
            val result = apiRepository.deleteMenu(menuId)
            hideLoading()

            if (result.status) {
                Toast.makeText(requireContext(), "Menu berhasil dihapus", Toast.LENGTH_SHORT).show()
                loadMenuData() // Refresh data
            } else {
                Toast.makeText(requireContext(), "Gagal menghapus: ${result.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        tableLayout.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showEmptyState() {
        emptyState.visibility = View.VISIBLE
        tableLayout.visibility = View.GONE
    }

    private fun hideEmptyState() {
        emptyState.visibility = View.GONE
        tableLayout.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        showEmptyState()
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }



/*
    private fun navigateToEditProduct(menu: MenuItem) {
        // Gunakan salah satu method di atas
        // Contoh dengan Serializable:
        val fragment = AddProdukFragment.newInstance(menu)
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_kelolamenu, fragment)
            .addToBackStack("Kelola Menu")
            .commit()
    }
 */


}
