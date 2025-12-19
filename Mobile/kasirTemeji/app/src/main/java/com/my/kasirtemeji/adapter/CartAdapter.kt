package com.my.kasirtemeji.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.my.kasirtemeji.R
import com.my.kasirtemeji.models.SelectedMenuItem
import com.my.kasirtemeji.util.ImageUtils
import com.my.kasirtemeji.util.ImageUtils.loadImageFromBase64

class CartAdapter(
    private var cartItems: MutableList<SelectedMenuItem> = mutableListOf(),
    private val onQuantityChange: (Int, Int) -> Unit,
    private val onRemoveItem: (Int) -> Unit,
    private val onAddNote: (Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pesanan, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position], position)
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateData(newList: List<SelectedMenuItem>) {
        cartItems.clear()
        cartItems.addAll(newList)
        notifyDataSetChanged()
    }

    fun addItem(item: SelectedMenuItem) {
        cartItems.add(item)
        notifyItemInserted(cartItems.size - 1)
    }

    fun removeItem(position: Int) {
        cartItems.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clearAll() {
        cartItems.clear()
        notifyDataSetChanged()
    }

    fun getItems(): List<SelectedMenuItem> = cartItems.toList()

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgMenuItem: ImageView = itemView.findViewById(R.id.img_menu_item)
        private val tvNamaProduk: TextView = itemView.findViewById(R.id.tv_nama_produk)
        private val tvHarga: TextView = itemView.findViewById(R.id.tv_harga)
        private val tvCatatan: TextView = itemView.findViewById(R.id.tv_catatan)
        private val tvKuantitas: TextView = itemView.findViewById(R.id.tv_kuantitas)
        private val btnKurang: ImageButton = itemView.findViewById(R.id.btn_kurang)
        private val btnTambah: ImageButton = itemView.findViewById(R.id.btn_tambah)
        private val btnHapus: ImageButton = itemView.findViewById(R.id.btn_hapus_item)

        fun bind(item: SelectedMenuItem, position: Int) {
            tvNamaProduk.text = item.menu.namaMenu
            tvHarga.text = "Rp ${item.menu.harga.toInt()}"
            tvKuantitas.text = item.quantity.toString()
            loadImageFromBase64(imgMenuItem, item.menu.gambarBase64)

            // Set catatan jika ada
            if (item.menu.namaMenu.contains("catatan")) {
                tvCatatan.visibility = View.VISIBLE
                tvCatatan.text = "Catatan" // Default note
            } else {
                tvCatatan.visibility = View.GONE
            }

            // Handle quantity buttons
            btnKurang.setOnClickListener {
                if (item.quantity > 1) {
                    onQuantityChange(position, item.quantity - 1)
                }
            }

            btnTambah.setOnClickListener {
                onQuantityChange(position, item.quantity + 1)
            }

            // Handle remove button
            btnHapus.setOnClickListener {
                onRemoveItem(position)
            }

            // Handle note click
            tvCatatan.setOnClickListener {
                onAddNote(position)
            }

            // Also make the note area clickable
            itemView.setOnClickListener {
                // You can show note dialog when item is clicked
                onAddNote(position)
            }
        }
    }
}