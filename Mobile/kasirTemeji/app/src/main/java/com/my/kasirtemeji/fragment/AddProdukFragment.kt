package com.my.kasirtemeji.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.my.kasirtemeji.R
import com.my.kasirtemeji.api.ApiRepository
import com.my.kasirtemeji.api.RetrofitInstance
import com.my.kasirtemeji.models.MenuItem
import com.my.kasirtemeji.models.MenuRequest
import com.my.kasirtemeji.util.NetworkUtils
import com.my.kasirtemeji.util.SessionManager
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.*

class AddProdukFragment : Fragment() {

    companion object {
        private const val ARG_MENU = "menu_data"

        fun newInstance(menu: MenuItem): AddProdukFragment {
            val fragment = AddProdukFragment()
            val args = Bundle()
            args.putSerializable(ARG_MENU, menu)
            fragment.arguments = args
            return fragment
        }
    }

    // Views
    private lateinit var etIdProduct: EditText
    private lateinit var etNameProduct: EditText
    private lateinit var etQuantity: EditText
    private lateinit var etPrice: EditText
    private lateinit var btnBatal: Button
    private lateinit var btnSimpan: Button
    private lateinit var uploadArea: LinearLayout
    private lateinit var imagePreview: ImageView
    private lateinit var txtDragDrop: TextView
    private lateinit var txtBrowse: TextView
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerStatus: Spinner
    private lateinit var progressBar: ProgressBar
    private lateinit var view: View

    // Variables
    private lateinit var apiRepository: ApiRepository
    private var selectedImageUri: Uri? = null
    private var selectedImageFile: File? = null
    private var isEditMode = false
    private var currentMenuId: Int? = null

    // Untuk mengambil gambar dari galeri
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                selectedImageUri = uri
                loadImagePreview(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Simpan view agar tidak membuat ulang
        if (!::view.isInitialized) {
            view = inflater.inflate(R.layout.kelolamenu2_spv, container, false)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cek apakah views sudah diinisialisasi
        if (!::etIdProduct.isInitialized) {
            setupViews(view)
            setupApiRepository()
            setupSpinners()
            checkEditMode()
            setupListeners()
            setupPriceFormatter()
        }
    }

    private fun setupViews(view: View) {
        etIdProduct = view.findViewById(R.id.et_id_product)
        etNameProduct = view.findViewById(R.id.et_name_product)
        etQuantity = view.findViewById(R.id.et_quantity)
        etPrice = view.findViewById(R.id.et_price_product)
        btnBatal = view.findViewById(R.id.btn_batal)
        btnSimpan = view.findViewById(R.id.btn_simpan)
        uploadArea = view.findViewById(R.id.upload_area)

        // Perbaiki: Gunakan findViewById untuk mendapatkan views yang sudah ada di XML
        imagePreview = view.findViewById(R.id.image_preview)
        txtDragDrop = view.findViewById(R.id.txt_drag_drop)
        txtBrowse = view.findViewById(R.id.txt_browse)
        progressBar = view.findViewById(R.id.progress_bar)

        // Jika views tidak ada di XML, buat programmatically hanya sekali
        if (imagePreview == null) {
            imagePreview = ImageView(requireContext()).apply {
                id = View.generateViewId()
                layoutParams = LinearLayout.LayoutParams(200, 200)
                scaleType = ImageView.ScaleType.CENTER_CROP
                visibility = View.GONE
            }
            uploadArea.addView(imagePreview, 0)
        }

        if (txtDragDrop == null) {
            txtDragDrop = TextView(requireContext()).apply {
                id = View.generateViewId()
                text = "Drag and Drop"
                textSize = 25f
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            uploadArea.addView(txtDragDrop)
        }

        if (txtBrowse == null) {
            txtBrowse = TextView(requireContext()).apply {
                id = View.generateViewId()
                text = "Browse Files"
                textSize = 20f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            uploadArea.addView(txtBrowse)
        }
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

    private fun setupSpinners() {
        // Setup kategori spinner
        val categories = listOf("Makanan", "Minuman", "Snack", "Signature", "Lainnya")
        spinnerCategory = Spinner(requireContext()).apply {
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }

        // Temukan container kategori dan ganti EditText dengan Spinner
        val categoryContainer = view.findViewById<LinearLayout>(R.id.category_container)
        if (categoryContainer != null) {
            val etCategory = view.findViewById<EditText>(R.id.et_category_product)
            if (etCategory != null) {
                categoryContainer.removeView(etCategory)
            }
            categoryContainer.addView(spinnerCategory, 1)
        } else {
            // Fallback: cari parent dari EditText kategori
            val etCategory = view.findViewById<EditText>(R.id.et_category_product)
            val parent = etCategory?.parent as? LinearLayout
            parent?.let {
                it.removeView(etCategory)
                it.addView(spinnerCategory, 0)
            }
        }

        // Setup status spinner
        val statuses = listOf("Tersedia", "Habis", "Tidak Tersedia")
        spinnerStatus = Spinner(requireContext()).apply {
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                statuses
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }

        // Tambah spinner status ke layout
        val formLayout = view.findViewById<LinearLayout>(R.id.form_container)
        if (formLayout != null) {
            val statusContainer = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginEnd = 8
                }
            }

            val statusLabel = TextView(requireContext()).apply {
                text = "Status"
                textSize = 14f
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 4
                }
            }

            statusContainer.addView(statusLabel)
            statusContainer.addView(spinnerStatus)
            formLayout.addView(statusContainer)
        }
    }

    private fun checkEditMode() {
        (arguments?.getSerializable(ARG_MENU) as? MenuItem)?.let { menu ->
            isEditMode = true
            currentMenuId = menu.id
            populateForm(menu)
        }
    }

    private fun populateForm(menu: MenuItem) {
        // Auto-generate ID based on existing ID
        etIdProduct.setText("M${menu.id.toString().padStart(3, '0')}")
        etIdProduct.isEnabled = false // ID tidak bisa diubah di edit mode

        etNameProduct.setText(menu.namaMenu)
        etQuantity.setText(menu.stok.toString())

        // Set kategori spinner
        val kategoriPosition = (spinnerCategory.adapter as? ArrayAdapter<String>)?.getPosition(menu.kategori) ?: 0
        spinnerCategory.setSelection(kategoriPosition)

        // Format harga
        val priceFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        etPrice.setText(priceFormat.format(menu.harga))

        // Set status spinner
        val statusPosition = (spinnerStatus.adapter as? ArrayAdapter<String>)?.getPosition(menu.status) ?: 0
        spinnerStatus.setSelection(statusPosition)

        // Load image if available
        menu.gambarBase64?.let { base64Image ->
            try {
                val imageBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT)
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imagePreview.setImageBitmap(bitmap)
                imagePreview.visibility = View.VISIBLE
                txtDragDrop.visibility = View.GONE
            } catch (e: Exception) {
                // Ignore error
            }
        }
    }

    private fun setupListeners() {
        // Upload area click listener
        uploadArea.setOnClickListener {
            pickImageFromGallery()
        }

        // Browse text click listener
        txtBrowse.setOnClickListener {
            pickImageFromGallery()
        }

        // Cancel button
        btnBatal.setOnClickListener {
            clearInput()
            parentFragmentManager.popBackStack()
        }

        // Save button
        btnSimpan.setOnClickListener {
            if (validateForm()) {
                if (isEditMode) {
                    updateMenu()
                } else {
                    createMenu()
                }
            }
        }
    }

    private fun setupPriceFormatter() {
        etPrice.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    etPrice.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[^\\d]".toRegex(), "")

                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDouble()
                        val formatted = NumberFormat.getNumberInstance(Locale.getDefault()).format(parsed)

                        current = formatted
                        etPrice.setText(formatted)
                        etPrice.setSelection(formatted.length)
                    }

                    etPrice.addTextChangedListener(this)
                }
            }
        })
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun loadImagePreview(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            imagePreview.setImageBitmap(bitmap)
            imagePreview.visibility = View.VISIBLE
            txtDragDrop.visibility = View.GONE

            // Convert URI to File
            selectedImageFile = createTempFileFromUri(uri)

            inputStream?.close()
        } catch (e: Exception) {
            showSnackbar("Gagal memuat gambar")
        }
    }

    private fun createTempFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("menu_image", ".jpg", requireContext().cacheDir)
            val outputStream = FileOutputStream(tempFile)

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            tempFile
        } catch (e: Exception) {
            null
        }
    }

    private fun validateForm(): Boolean {
        // Validasi nama produk
        val namaProduk = etNameProduct.text.toString().trim()
        if (namaProduk.isEmpty()) {
            etNameProduct.error = "Nama produk harus diisi"
            return false
        }

        // Validasi kategori
        val kategori = spinnerCategory.selectedItem.toString()
        if (kategori.isEmpty()) {
            showSnackbar("Kategori harus dipilih")
            return false
        }

        // Validasi stok
        val stokStr = etQuantity.text.toString().trim()
        if (stokStr.isEmpty()) {
            etQuantity.error = "Stok harus diisi"
            return false
        }

        val stok = stokStr.toIntOrNull()
        if (stok == null || stok < 0) {
            etQuantity.error = "Stok harus angka positif"
            return false
        }

        // Validasi harga
        val hargaStr = etPrice.text.toString().replace("[^\\d]".toRegex(), "")
        if (hargaStr.isEmpty()) {
            etPrice.error = "Harga harus diisi"
            return false
        }

        val harga = hargaStr.toDoubleOrNull()
        if (harga == null || harga <= 0) {
            etPrice.error = "Harga harus lebih dari 0"
            return false
        }

        // Validasi status
        val status = spinnerStatus.selectedItem.toString()
        if (status.isEmpty()) {
            showSnackbar("Status harus dipilih")
            return false
        }

        return true
    }

    private fun createMenu() {
        showLoading(true)

        lifecycleScope.launch {
            val namaMenu = etNameProduct.text.toString().trim()
            val kategori = spinnerCategory.selectedItem.toString()
            val stok = etQuantity.text.toString().trim().toInt()
            val harga = etPrice.text.toString().replace("[^\\d]".toRegex(), "").toDouble()
            val status = spinnerStatus.selectedItem.toString()

            // KONVERSI GAMBAR KE BASE64
            val gambarBase64 = if (selectedImageFile != null) {
                convertFileToBase64(selectedImageFile!!)
            } else {
                null
            }

            // GUNAKAN FUNGSI createMenu() BUKAN createMenuWithImage()
            val result = apiRepository.createMenu(
                MenuRequest(
                    namaMenu = namaMenu,
                    kategori = kategori,
                    stok = stok,
                    harga = harga,
                    status = status,
                    gambarBase64 = gambarBase64  // <-- INI BASE64!
                )
            )

            hideLoading()

            if (result.status) {
                showSnackbar("Menu berhasil ditambahkan")
                parentFragmentManager.popBackStack()
            } else {
                showSnackbar("Gagal menambahkan menu: ${result.message}")
            }
        }
    }

    // Fungsi untuk konversi File ke Base64
    private fun convertFileToBase64(file: File): String? {
        return try {
            val inputStream = file.inputStream()
            val bytes = inputStream.readBytes()
            inputStream.close()
            android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }

    private fun updateMenu() {
        showLoading(true)

        lifecycleScope.launch {
            val menuId = currentMenuId ?: return@launch
            val namaMenu = etNameProduct.text.toString().trim()
            val kategori = spinnerCategory.selectedItem.toString()
            val stok = etQuantity.text.toString().trim().toInt()
            val harga = etPrice.text.toString().replace("[^\\d]".toRegex(), "").toDouble()
            val status = spinnerStatus.selectedItem.toString()

            // KONVERSI GAMBAR KE BASE64
            val gambarBase64 = if (selectedImageFile != null) {
                convertFileToBase64(selectedImageFile!!)
            } else {
                // Jika tidak ada gambar baru, gunakan gambar lama (jika ada)
                val currentMenu = arguments?.getSerializable(ARG_MENU) as? MenuItem
                currentMenu?.gambarBase64
            }

            // GUNAKAN FUNGSI updateMenu()
            val result = apiRepository.updateMenu(
                menuId = menuId,
                menuRequest = MenuRequest(
                    namaMenu = namaMenu,
                    kategori = kategori,
                    stok = stok,
                    harga = harga,
                    status = status,
                    gambarBase64 = gambarBase64  // <-- INI BASE64!
                )
            )

            hideLoading()

            if (result.status) {
                showSnackbar("Menu berhasil diupdate")
                parentFragmentManager.popBackStack()
            } else {
                showSnackbar("Gagal mengupdate menu: ${result.message}")
            }
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnSimpan.isEnabled = !show
        btnBatal.isEnabled = !show
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
        btnSimpan.isEnabled = true
        btnBatal.isEnabled = true
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    private fun clearInput(){

        etNameProduct.setText("")
        spinnerCategory.setSelection(0)
        etQuantity.setText("")
        etPrice.setText("")
        spinnerStatus.setSelection(0)
        imagePreview.setImageDrawable(null)
        imagePreview.setVisibility(View.GONE)
    }
}