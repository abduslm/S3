package com.my.kasirtemeji.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.my.kasirtemeji.MainActivity
import com.my.kasirtemeji.R
import com.my.kasirtemeji.adapter.CartAdapter
import com.my.kasirtemeji.adapter.MenuAdapter
import com.my.kasirtemeji.api.ApiRepository
import com.my.kasirtemeji.api.RetrofitInstance
import com.my.kasirtemeji.models.*
import com.my.kasirtemeji.util.NetworkUtils
import com.my.kasirtemeji.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

class MainKasir : AppCompatActivity() {

    // Deklarasi semua view
    private lateinit var apiRepository: ApiRepository

    // Header views
    private lateinit var etSearch: EditText
    private lateinit var btnDaftarPesanan: androidx.appcompat.widget.AppCompatButton
    private lateinit var tvRestaurantName: TextView
    private lateinit var tvDateUser: TextView

    // Sidebar views
    private lateinit var tvAllMenu: TextView
    private lateinit var tvMakanan: TextView
    private lateinit var tvMinuman: TextView
    private lateinit var tvSignature: TextView
    private lateinit var tvLainnya: TextView
    private lateinit var btnKembali: androidx.appcompat.widget.AppCompatButton

    // Menu list views
    private lateinit var rvMenu: RecyclerView
    private lateinit var pbLoadingMenu: ProgressBar
    private lateinit var tvEmptyMenu: TextView

    // Cart views
    private lateinit var rvCartItems: RecyclerView
    private lateinit var tvEmptyCart: TextView

    // Summary views
    private lateinit var tvSubtotalValue: TextView
    private lateinit var tvPpnValue: TextView
    private lateinit var cbPpn: CheckBox
    private lateinit var tvChangeValue: TextView
    private lateinit var etCashAmount: EditText
    private lateinit var btnCash: TextView
    private lateinit var btnQris: TextView
    private lateinit var tvItemCount: TextView
    private lateinit var tvOrderTotal: TextView
    private lateinit var btnOrderNow: androidx.appcompat.widget.AppCompatButton


    // Adapters
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var cartAdapter: CartAdapter

    // Variables
    private var selectedCategory: String? = null
    private val cartItems = mutableListOf<SelectedMenuItem>()
    private var searchQuery: String = ""
    private var selectedPaymentMethod = "cash"
    private var ppnPercentage = 0.12 // 12%
    private var usePpn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kaka)

        // Initialize API Repository
        initApiRepository()

        // Initialize all views
        initViews()

        // Setup UI components
        setupUI()

        // Load initial data
        loadMenuData()

        // Setup auto refresh
        setupAutoRefresh()
    }

    private fun initApiRepository() {
        val apiService = RetrofitInstance.apiService
        val networkUtils = NetworkUtils(this)
        val sessionManager = SessionManager.getInstance()

        apiRepository = ApiRepository(apiService, networkUtils, sessionManager)
    }

    private fun initViews() {
        // Header views
        etSearch = findViewById(R.id.et_search)
        btnDaftarPesanan = findViewById(R.id.btn_daftar_pesanan)
        tvRestaurantName = findViewById(R.id.tv_restaurant_name)
        tvDateUser = findViewById(R.id.tv_date_user)

        // Sidebar views
        tvAllMenu = findViewById(R.id.tv_all_menu)
        tvMakanan = findViewById(R.id.tv_makanan)
        tvMinuman = findViewById(R.id.tv_minuman)
        tvSignature = findViewById(R.id.tv_signature)
        tvLainnya = findViewById(R.id.tv_lainnya)
        btnKembali = findViewById(R.id.btn_kembali)

        // Menu list views
        rvMenu = findViewById(R.id.rv_menu)
        pbLoadingMenu = findViewById(R.id.pb_loading_menu)
        tvEmptyMenu = findViewById(R.id.tv_empty_menu)

        // Cart views
        rvCartItems = findViewById(R.id.rv_cart_items)
        tvEmptyCart = findViewById(R.id.tv_empty_cart)

        // Summary views
        tvSubtotalValue = findViewById(R.id.tv_subtotal_value)
        tvPpnValue = findViewById(R.id.tv_ppn_value)
        cbPpn = findViewById(R.id.cb_ppn)
        tvChangeValue = findViewById(R.id.tv_change_value)
        etCashAmount = findViewById(R.id.et_cash_amount)
        btnCash = findViewById(R.id.btn_cash)
        btnQris = findViewById(R.id.btn_qris)
        tvItemCount = findViewById(R.id.tv_item_count)
        tvOrderTotal = findViewById(R.id.tv_order_total)
        btnOrderNow = findViewById(R.id.btn_order_now)
    }

    private fun setupUI() {
        // Setup menu RecyclerView
        setupMenuRecyclerView()

        // Setup cart RecyclerView
        setupCartRecyclerView()

        // Setup category selection
        setupCategorySelection()

        // Setup search functionality
        setupSearch()

        // Setup button listeners
        setupButtonListeners()

        // Setup payment method
        setupPaymentMethod()

        // Setup cash input listener
        setupCashInputListener()

        // Setup PPN checkbox
        setupPpnCheckbox()
    }

    private fun setupMenuRecyclerView() {
        menuAdapter = MenuAdapter(mutableListOf()) { menu ->
            addToCart(menu)
        }

        rvMenu.apply {
            layoutManager = GridLayoutManager(this@MainKasir, 3)
            adapter = menuAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupCartRecyclerView() {
        cartAdapter = CartAdapter(
            cartItems,
            onQuantityChange = { position, newQuantity ->
                updateCartItemQuantity(position, newQuantity)
            },
            onRemoveItem = { position ->
                removeFromCart(position)
            },
            onAddNote = { position ->
                showNoteDialog(position)
            }
        )

        rvCartItems.apply {
            layoutManager = LinearLayoutManager(this@MainKasir)
            adapter = cartAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupCategorySelection() {
        // Set click listeners untuk semua kategori
        val allCategoryViews = listOf(tvAllMenu, tvMakanan, tvMinuman, tvSignature, tvLainnya)

        tvAllMenu.setOnClickListener {
            selectCategory(null, allCategoryViews)
        }

        tvMakanan.setOnClickListener {
            selectCategory("Makanan", allCategoryViews)
        }

        tvMinuman.setOnClickListener {
            selectCategory("Minuman", allCategoryViews)
        }

        tvSignature.setOnClickListener {
            selectCategory("Signature", allCategoryViews)
        }

        tvLainnya.setOnClickListener {
            selectCategory("Lainnya", allCategoryViews)
        }

        // Default pilih "Semua Menu"
        selectCategory(null, allCategoryViews)
    }

    private fun selectCategory(category: String?, categoryViews: List<TextView>) {
        selectedCategory = category

        // Reset semua background dan text color
        categoryViews.forEach { tv ->
            tv.setBackgroundResource(android.R.color.transparent)
            tv.setTextColor(resources.getColor(R.color.brown_primary))
        }

        // Highlight kategori yang dipilih
        when (category) {
            null -> {
                tvAllMenu.setBackgroundResource(R.drawable.bg_category_selected)
                tvAllMenu.setTextColor(resources.getColor(android.R.color.white))
            }
            "Makanan" -> {
                tvMakanan.setBackgroundResource(R.drawable.bg_category_selected)
                tvMakanan.setTextColor(resources.getColor(android.R.color.white))
            }
            "Minuman" -> {
                tvMinuman.setBackgroundResource(R.drawable.bg_category_selected)
                tvMinuman.setTextColor(resources.getColor(android.R.color.white))
            }
            "Signature" -> {
                tvSignature.setBackgroundResource(R.drawable.bg_category_selected)
                tvSignature.setTextColor(resources.getColor(android.R.color.white))
            }
            "Lainnya" -> {
                tvLainnya.setBackgroundResource(R.drawable.bg_category_selected)
                tvLainnya.setTextColor(resources.getColor(android.R.color.white))
            }
        }

        // Reload data menu
        loadMenuData()
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s.toString()
                // Debounce search (500ms)
                etSearch.removeCallbacks(searchRunnable)
                etSearch.postDelayed(searchRunnable, 500)
            }
        })
    }

    private val searchRunnable = Runnable {
        loadMenuData()
    }

    private fun setupButtonListeners() {
        // Kembali button
        btnKembali.setOnClickListener {
            showLogoutConfirmation()
        }

        // Daftar Pesanan button
        btnDaftarPesanan.setOnClickListener {
            Toast.makeText(this, "Fitur Daftar Pesanan akan segera tersedia", Toast.LENGTH_SHORT).show()
        }

        // Order Now button
        btnOrderNow.setOnClickListener {
            processOrder()
        }
    }

    private fun setupPaymentMethod() {
        btnCash.setOnClickListener {
            selectPaymentMethod("cash")
        }

        btnQris.setOnClickListener {
            selectPaymentMethod("qris")
        }

        // Default pilih cash
        selectPaymentMethod("cash")
    }

    private fun setupPpnCheckbox() {
        cbPpn.setOnCheckedChangeListener { _, isChecked ->
            usePpn = isChecked
            updateSummary()
        }
    }

    private fun selectPaymentMethod(method: String) {
        selectedPaymentMethod = method

        // Reset background
        btnCash.setBackgroundResource(android.R.color.transparent)
        btnQris.setBackgroundResource(android.R.color.transparent)

        // Set selected background
        when (method) {
            "cash" -> {
                btnCash.setBackgroundResource(R.drawable.bg_metode_active)
                // Tampilkan input cash jika cash dipilih
                findViewById<LinearLayout>(R.id.ll_cash_received_row).visibility = View.VISIBLE
                findViewById<LinearLayout>(R.id.ll_change_row).visibility = View.VISIBLE
            }
            "qris" -> {
                btnQris.setBackgroundResource(R.drawable.bg_metode_active)
                // Sembunyikan input cash jika qris dipilih
                findViewById<LinearLayout>(R.id.ll_cash_received_row).visibility = View.GONE
                findViewById<LinearLayout>(R.id.ll_change_row).visibility = View.GONE
            }
        }
    }

    private fun setupCashInputListener() {
        etCashAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                calculateChange()
            }
        })
    }

    private fun setupAutoRefresh() {
        // Auto refresh setiap 30 detik
        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                loadMenuData()
                // Jadwalkan ulang
                Handler(Looper.getMainLooper()).postDelayed(this, 30000)
            }
        }, 30000)
    }

    private fun loadMenuData() {
        pbLoadingMenu.visibility = View.VISIBLE
        tvEmptyMenu.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            val result = apiRepository.searchMenus(searchQuery, selectedCategory)

            withContext(Dispatchers.Main) {
                pbLoadingMenu.visibility = View.GONE

                if (result.status) {
                    val menus = result.data ?: emptyList()
                    menuAdapter.updateData(menus)

                    if (menus.isEmpty()) {
                        tvEmptyMenu.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(
                        this@MainKasir,
                        result.message ?: "Gagal memuat menu",
                        Toast.LENGTH_SHORT
                    ).show()
                    tvEmptyMenu.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun addToCart(menu: MenuItem) {
        // Cek apakah menu TERSEDIA (bukan "Habis" atau "Tidak Tersedia")
        if (menu.status != "Tersedia") {
            val statusText = when(menu.status) {
                "Habis" -> "habis stok"
                "Tidak Tersedia" -> "tidak tersedia"
                else -> menu.status.lowercase()
            }
            Toast.makeText(this, "${menu.namaMenu} $statusText", Toast.LENGTH_SHORT).show()
            return
        }

        // Cek apakah item sudah ada di cart
        val existingIndex = cartItems.indexOfFirst { it.menu.id == menu.id }

        if (existingIndex != -1) {
            // Increment quantity jika sudah ada
            cartItems[existingIndex].increment()
            cartAdapter.notifyItemChanged(existingIndex)
        } else {
            // Tambah item baru
            val selectedItem = SelectedMenuItem(menu)
            cartItems.add(selectedItem)
            cartAdapter.notifyItemInserted(cartItems.size - 1)
        }

        updateCartUI()
        updateSummary()
        Toast.makeText(this, "${menu.namaMenu} ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
    }

    private fun updateCartItemQuantity(position: Int, newQuantity: Int) {
        if (position in cartItems.indices) {
            val item = cartItems[position]

            // Perbolehkan order meskipun stok 0 di cart (untuk kasir bisa override)
            item.quantity = newQuantity
            cartAdapter.notifyItemChanged(position)
            updateSummary()
        }
    }

    private fun removeFromCart(position: Int) {
        if (position in cartItems.indices) {
            cartItems.removeAt(position)
            cartAdapter.notifyItemRemoved(position)
            updateCartUI()
            updateSummary()
        }
    }

    private fun clearCart() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Keranjang sudah kosong", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Hapus Semua Pesanan")
            .setMessage("Apakah Anda yakin ingin menghapus semua item dari keranjang?")
            .setPositiveButton("Ya, Hapus") { dialog, _ ->
                cartItems.clear()
                cartAdapter.notifyDataSetChanged()
                updateCartUI()
                updateSummary()
                Toast.makeText(this, "Semua item dihapus dari keranjang", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updateCartUI() {
        if (cartItems.isEmpty()) {
            tvEmptyCart.visibility = View.VISIBLE
            rvCartItems.visibility = View.GONE
        } else {
            tvEmptyCart.visibility = View.GONE
            rvCartItems.visibility = View.VISIBLE

            // Update item count
            val totalItems = cartItems.sumOf { it.quantity }
            tvItemCount.text = "$totalItems items"
        }
    }

    private fun updateSummary() {
        val subtotal = cartItems.sumOf { it.subtotal }
        val ppn = if (usePpn) subtotal * ppnPercentage else 0.0
        val total = subtotal + ppn

        // Format angka dengan pemisah ribuan
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))

        // Update tampilan
        tvSubtotalValue.text = "Rp ${formatter.format(subtotal)}"
        tvPpnValue.text = "Rp ${formatter.format(ppn)}"
        tvOrderTotal.text = "Rp ${formatter.format(total)}"

        // Tampilkan/sembunyikan PPN value berdasarkan checkbox
        tvPpnValue.visibility = if (usePpn) View.VISIBLE else View.GONE

        // Hitung kembalian
        calculateChange()
    }

    private fun calculateChange() {
        try {
            val totalText = tvOrderTotal.text.toString()
                .replace("Rp", "")
                .replace(".", "")
                .trim()

            val total = totalText.toDoubleOrNull() ?: 0.0

            val cashAmount = etCashAmount.text.toString().toDoubleOrNull() ?: 0.0

            val change = cashAmount - total

            val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))

            if (change >= 0) {
                tvChangeValue.text = "Rp ${formatter.format(change)}"
                tvChangeValue.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            } else {
                tvChangeValue.text = "Kurang: Rp ${formatter.format(-change)}"
                tvChangeValue.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
        } catch (e: Exception) {
            tvChangeValue.text = "Rp 0"
        }
    }

    private fun showNoteDialog(position: Int) {
        if (position !in cartItems.indices) return

        val item = cartItems[position]

        val dialogView = layoutInflater.inflate(R.layout.dialog_note, null)
        val etNote = dialogView.findViewById<EditText>(R.id.et_note)

        AlertDialog.Builder(this)
            .setTitle("Catatan untuk ${item.menu.namaMenu}")
            .setView(dialogView)
            .setPositiveButton("Simpan") { dialog, _ ->
                val note = etNote.text.toString()
                // TODO: Simpan catatan ke item
                Toast.makeText(this, "Catatan disimpan", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun processOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Keranjang kosong", Toast.LENGTH_SHORT).show()
            return
        }

        // Validasi payment method
        if (selectedPaymentMethod == "cash") {
            val cashAmount = etCashAmount.text.toString().toDoubleOrNull() ?: 0.0
            val totalText = tvOrderTotal.text.toString()
                .replace("Rp", "")
                .replace(".", "")
                .trim()
            val total = totalText.toDoubleOrNull() ?: 0.0

            if (cashAmount < total) {
                Toast.makeText(this, "Jumlah uang kurang", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Tampilkan konfirmasi sebelum order
        showOrderConfirmation()
    }

    private fun showOrderConfirmation() {
        val totalText = tvOrderTotal.text.toString()
        val itemCount = cartItems.sumOf { it.quantity }
        val ppnText = if (usePpn) "dengan PPN 12%" else "tanpa PPN"
        val paymentMethod = if (selectedPaymentMethod == "cash") "Cash" else "QRIS"

        val message = """
            Anda akan memesan $itemCount item $ppnText.
            
            Total: $totalText
            Metode bayar: $paymentMethod
            
            Apakah Anda yakin ingin memproses pesanan ini?
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Pesanan")
            .setMessage(message)
            .setPositiveButton("Ya, Proses") { dialog, _ ->
                dialog.dismiss()
                createOrder()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun createOrder() {
        // Create order request
        val orderItems = cartItems.map { item ->
            OrderItemRequest(item.menu.id, item.quantity)
        }

        val orderRequest = OrderRequest(
            items = orderItems,
            statusPesanan = "Pending"
        )

        // Show loading dialog
        val progressDialog = AlertDialog.Builder(this)
            .setView(R.layout.dialog_loading)
            .setCancelable(false)
            .create()

        progressDialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            val result = apiRepository.createOrder(orderRequest)

            withContext(Dispatchers.Main) {
                progressDialog.dismiss()

                if (result.status) {
                    // Success - tampilkan dialog sukses
                    showOrderSuccessDialog()
                } else {
                    // Error
                    AlertDialog.Builder(this@MainKasir)
                        .setTitle("Gagal Memproses Pesanan")
                        .setMessage(result.message ?: "Gagal memproses pesanan")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }
    }

    private fun showOrderSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("✅ Pesanan Berhasil")
            .setMessage("Pesanan berhasil diproses dan dikirim ke dapur!")
            .setPositiveButton("OK") { dialog, _ ->
                // Clear cart
                cartItems.clear()
                cartAdapter.notifyDataSetChanged()
                updateCartUI()
                updateSummary()

                // Reset cash input
                etCashAmount.setText("30000")

                // Refresh menu untuk update stok
                loadMenuData()

                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        etSearch.removeCallbacks(searchRunnable)
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
        showExitAplikasi()
    }

    private fun showExitAplikasi() {
        AlertDialog.Builder(this)
            .setTitle("Keluar Aplikasi")
            .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
            .setPositiveButton("Ya") { _, _ ->
                finishAffinity()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}