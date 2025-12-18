package com.my.kasirtemeji.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.my.kasirtemeji.R
import com.my.kasirtemeji.models.OrderDetailItem

class DetailPesananAdapter(
    private var items: List<OrderDetailItem> = emptyList()
) : RecyclerView.Adapter<DetailPesananAdapter.DetailViewHolder>() {

    inner class DetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageItem: ImageView = itemView.findViewById(R.id.top_view_de)
        val textNamaItem: TextView = itemView.findViewById(R.id.mierendang1)
        val textKeterangan: TextView = itemView.findViewById(R.id.keterangan)
        val textJumlah: TextView = itemView.findViewById(R.id.satu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pesanankasir, parent, false)
        return DetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val item = items[position]

        // Set data ke view
        holder.textNamaItem.text = item.namaMenu
        holder.textJumlah.text = item.jumlah.toString()

        // Default keterangan jika tidak ada
        holder.textKeterangan.text = "Tanpa keterangan"

        // Set gambar berdasarkan nama menu
        val imageRes = when {
            item.namaMenu.contains("rendang", ignoreCase = true) -> R.drawable.indomierendangxml
            item.namaMenu.contains("goreng", ignoreCase = true) -> R.drawable.indomierendangxml
            item.namaMenu.contains("teh", ignoreCase = true) -> R.drawable.es_teh
            else -> R.drawable.pesanan10 // Default icon
        }
        holder.imageItem.setImageResource(imageRes)
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<OrderDetailItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}