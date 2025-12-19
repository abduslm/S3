package com.my.kasirtemeji.activity

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.my.kasirtemeji.R
import com.my.kasirtemeji.adapter.DetailPesananAdapter
import com.my.kasirtemeji.adapter.PesananDapurAdapter
import com.my.kasirtemeji.api.RetrofitInstance
import com.my.kasirtemeji.models.Order
import com.my.kasirtemeji.models.OrderDetailItem
import com.my.kasirtemeji.models.UpdateOrderStatusRequest
import com.my.kasirtemeji.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainDapur : AppCompatActivity() {

    // === VIEW ===
    private lateinit var btnBack: Button
    private lateinit var btnInProgress: Button
    private lateinit var btnSelesai: Button
    private lateinit var rvPesanan: RecyclerView
    private lateinit var rvDetailPesanan: RecyclerView

    // === ADAPTER ===
    private lateinit var pesananAdapter: PesananDapurAdapter
    private lateinit var detailAdapter: DetailPesananAdapter

    // === DATA ===
    private var pesananList = mutableListOf<Order>()
    private lateinit var sessionManager: SessionManager
    private var selectedOrder: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_dapur)

        sessionManager = SessionManager.getInstance()!!

        initViews()
        setupRecyclerView()
        loadPesananDapur()
        setupAction()
    }

    // ================= INIT =================
    private fun initViews() {
        btnBack = findViewById(R.id.background)
        btnInProgress = findViewById(R.id.buttonprogres)
        btnSelesai = findViewById(R.id.buttonselesai)

        rvPesanan = findViewById(R.id.recyclerView_daftar_pesanan)
        rvDetailPesanan = findViewById(R.id.recyclerView_detail_pesanan)
    }

    private fun setupRecyclerView() {
        pesananAdapter = PesananDapurAdapter(pesananList) { order ->
            selectedOrder = order
            showDetailPesanan(order.details)
        }

        rvPesanan.layoutManager = LinearLayoutManager(this)
        rvPesanan.adapter = pesananAdapter

        detailAdapter = DetailPesananAdapter(emptyList())
        rvDetailPesanan.layoutManager = LinearLayoutManager(this)
        rvDetailPesanan.adapter = detailAdapter
    }

    private fun setupAction() {
        btnBack.setOnClickListener { finish() }

        btnInProgress.setOnClickListener {
            selectedOrder?.let {
                updateStatus(it, "SEDANG DIPROSES")
            }
        }

        btnSelesai.setOnClickListener {
            selectedOrder?.let {
                updateStatus(it, "SELESAI")
            }
        }
    }

    // ================= DATA =================
    private fun showDetailPesanan(details: List<OrderDetailItem>) {
        detailAdapter.updateItems(details)
    }
    private fun loadPesananDapur() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val token = sessionManager.getToken() ?: return@launch

                val response = RetrofitInstance.apiService.getOrders(
                    token = "Bearer $token",
                    status = "PENDING,SEDANG DIPROSES"
                )

                if (response.isSuccessful) {
                    pesananList.clear()
                    response.body()?.data?.pesanan?.let {
                        pesananList.addAll(it)
                    }
                    pesananAdapter.notifyDataSetChanged()
                }

            } catch (e: Exception) {
                Toast.makeText(this@MainDapur, "Gagal load data", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun updateStatus(order: Order, status: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val token = sessionManager.getToken() ?: return@launch

                RetrofitInstance.apiService.updateOrderStatus(
                    token = "Bearer $token",
                    orderId = order.id,
                    updateRequest = UpdateOrderStatusRequest(status)
                )

                if (status == "SELESAI") {
                    pesananList.remove(order)
                    detailAdapter.updateItems(emptyList())
                } else {
                    val index = pesananList.indexOf(order)
                    pesananList[index] = order.copy(statusPesanan = status)
                }

                pesananAdapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Toast.makeText(this@MainDapur, "Gagal update status", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
