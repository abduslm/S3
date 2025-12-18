package com.my.kasirtemeji.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.my.kasirtemeji.R
import com.my.kasirtemeji.models.Wifi

class WifiAdapter(
    private var wifiList: List<Wifi> = emptyList(),
    private val onEditClick: (Wifi) -> Unit,
    private val onDeleteClick: (Wifi) -> Unit
) : RecyclerView.Adapter<WifiAdapter.WifiViewHolder>() {

    class WifiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWifiName: TextView = itemView.findViewById(R.id.wifi_name)
        val btnEdit: ImageView = itemView.findViewById(R.id.btn_edit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wifi_list, parent, false)
        return WifiViewHolder(view)
    }

    override fun onBindViewHolder(holder: WifiViewHolder, position: Int) {
        val wifi = wifiList[position]

        holder.tvWifiName.text = wifi.namaWifi

        // Edit button click
        holder.btnEdit.setOnClickListener {
            onEditClick(wifi)
        }

        // Delete button click
        holder.btnDelete.setOnClickListener {
            onDeleteClick(wifi)
        }

        // Item click (select wifi)
        holder.itemView.setOnClickListener {
            onEditClick(wifi)
        }
    }

    override fun getItemCount(): Int = wifiList.size

    fun updateData(newList: List<Wifi>) {
        wifiList = newList
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Wifi = wifiList[position]
}