package com.my.kasirtemeji.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.my.kasirtemeji.R
import com.my.kasirtemeji.models.MenuItem
import com.my.kasirtemeji.util.ImageUtils // Import class ImageUtils

class MenuAdapter(
    private val menuList: MutableList<MenuItem> = mutableListOf(),
    private val onItemClick: (MenuItem) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = menuList[position]
        holder.bind(menu)
    }

    override fun getItemCount(): Int = menuList.size

    fun updateData(newList: List<MenuItem>) {
        menuList.clear()
        menuList.addAll(newList)
        notifyDataSetChanged()
    }

    fun addData(newList: List<MenuItem>) {
        val startPosition = menuList.size
        menuList.addAll(newList)
        notifyItemRangeInserted(startPosition, newList.size)
    }

    fun clearData() {
        menuList.clear()
        notifyDataSetChanged()
    }

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardViews)
        private val imgMenu: ImageView = itemView.findViewById(R.id.imgMenu)
        private val txtNama: TextView = itemView.findViewById(R.id.txtNama)
        private val txtHarga: TextView = itemView.findViewById(R.id.txtHarga)
        private val txtStok: TextView = itemView.findViewById(R.id.txtStok)

        fun bind(menu: MenuItem) {
            txtNama.text = menu.namaMenu
            txtHarga.text = "Rp ${menu.harga.toInt()}"

            // Tampilkan stok
            txtStok.text = "Stok: ${menu.stok}"

            // Load image menggunakan static method
            ImageUtils.loadImageFromBase64(imgMenu, menu.gambarBase64)

            // Set background berdasarkan stok
            if (menu.stok <= 0) {
                // Stok 0 - background merah
                cardView.setCardBackgroundColor(itemView.resources.getColor(R.color.light_red))
                txtStok.setTextColor(itemView.resources.getColor(R.color.dark_red))
            } else if (menu.stok < 5) {
                // Stok rendah - background kuning
                cardView.setCardBackgroundColor(itemView.resources.getColor(R.color.light_yellow))
                txtStok.setTextColor(itemView.resources.getColor(R.color.dark_orange))
            } else {
                // Stok normal - background putih
                cardView.setCardBackgroundColor(itemView.resources.getColor(android.R.color.white))
                txtStok.setTextColor(itemView.resources.getColor(R.color.gray))
            }

            // Tampilkan status jika tidak "Tersedia"
            if (menu.status != "Tersedia") {
                txtStok.text = "Status: ${menu.status}"
            }

            // Handle click
            itemView.setOnClickListener {
                // Hanya bisa klik jika menu "Tersedia"
                if (menu.status == "Tersedia") {
                    onItemClick(menu)
                } else {
                    // Tampilkan pesan mengapa tidak bisa diklik
                    val statusText = when(menu.status) {
                        "Habis" -> "habis stok"
                        "Tidak Tersedia" -> "tidak tersedia"
                        else -> menu.status.lowercase()
                    }
                    android.widget.Toast.makeText(
                        itemView.context,
                        "${menu.namaMenu} $statusText",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}