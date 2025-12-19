package com.my.kasirtemeji.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.my.kasirtemeji.R
import com.my.kasirtemeji.models.Order
import com.my.kasirtemeji.models.OrderDetailItem
import java.text.SimpleDateFormat
import java.util.Locale

class PesananDapurAdapter(
    private var pesananList: List<Order> = emptyList(),
    private val onItemSelected: (Order) -> Unit
) : RecyclerView.Adapter<PesananDapurAdapter.PesananViewHolder>() {

    inner class PesananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textWaktu: TextView = itemView.findViewById(R.id.text_waktu)
        val iconPesanan: ImageView = itemView.findViewById(R.id.icon_pesanan)
        val textNamaPesanan: TextView = itemView.findViewById(R.id.text_nama_pesanan)
        val textStatus: TextView = itemView.findViewById(R.id.text_status)
        val textDaftarItem: TextView = itemView.findViewById(R.id.text_daftar_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PesananViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daftar_pesanan, parent, false)
        return PesananViewHolder(view)
    }

    override fun onBindViewHolder(holder: PesananViewHolder, position: Int) {
        val pesanan = pesananList[position]

        // Format data
        holder.textWaktu.text = formatWaktu(pesanan.tanggalPesanan)
        holder.textNamaPesanan.text = "Pesanan ${pesanan.id} - ${pesanan.namaKasir}"
        holder.textStatus.text = pesanan.statusPesanan
        holder.textDaftarItem.text = formatItems(pesanan.details)

        // Set icon berdasarkan status
        setIconByStatus(holder.iconPesanan, pesanan.statusPesanan)

        // Set warna status
        setStatusColor(holder.textStatus, pesanan.statusPesanan)

        // Klik item untuk pilih pesanan
        holder.itemView.setOnClickListener {
            onItemSelected(pesanan)
        }
    }

    override fun getItemCount(): Int = pesananList.size

    fun updateData(newList: List<Order>) {
        pesananList = newList
        notifyDataSetChanged()
    }

    private fun formatWaktu(tanggalPesanan: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH.mm", Locale.getDefault())
            val date = inputFormat.parse(tanggalPesanan)
            outputFormat.format(date ?: return "")
        } catch (e: Exception) {
            tanggalPesanan
        }
    }

    private fun formatItems(details: List<OrderDetailItem>): String {
        val maxItems = 2 // Tampilkan maksimal 2 item
        val items = details.take(maxItems).joinToString(" + ") { item ->
            "${item.namaMenu} ${item.jumlah}"
        }

        if (details.size > maxItems) {
            return "$items + ${details.size - maxItems} lainnya"
        }
        return items
    }

    private fun setIconByStatus(imageView: ImageView, status: String) {
        val iconRes = when (status.uppercase()) {
            "PENDING" -> R.drawable.pesanan10 // Ganti dengan icon yang sesuai
            "SEDANG DIPROSES", "SEDANG_DIPROSES" -> R.drawable.pesanan10 // Default dulu
            "SELESAI" -> R.drawable.pesanan10 // Default dulu
            "DIBATALKAN" -> R.drawable.pesanan10 // Default dulu
            else -> R.drawable.pesanan10
        }
        imageView.setImageResource(iconRes)
    }

    private fun setStatusColor(textView: TextView, status: String) {
        val color = when (status.uppercase()) {
            "PENDING" -> Color.parseColor("#FF9800") // Orange
            "SEDANG DIPROSES", "SEDANG_DIPROSES" -> Color.parseColor("#2196F3") // Blue
            "SELESAI" -> Color.parseColor("#4CAF50") // Green
            "DIBATALKAN" -> Color.parseColor("#F44336") // Red
            else -> Color.parseColor("#FF9800")
        }
        textView.setTextColor(color)
    }
}