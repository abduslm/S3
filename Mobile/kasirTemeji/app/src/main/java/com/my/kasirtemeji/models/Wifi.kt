package com.my.kasirtemeji.models

import com.google.gson.annotations.SerializedName

/// models.kt - tambahkan di bagian yang sesuai

/**
 * Model untuk WiFi data
 */
data class Wifi(
    @SerializedName("id") val id: Int,
    @SerializedName("nama_wifi") val namaWifi: String,
    @SerializedName("password") val password: String,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

/**
 * Response untuk get WiFi list
 */
data class WifiResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: WifiData?,
    @SerializedName("timestamp") val timestamp: String? = null
)

/**
 * Data wrapper untuk WiFi operations
 */
data class WifiData(
    @SerializedName("wifi_list") val wifiList: List<Wifi>? = null,
    @SerializedName("wifi") val wifi: Wifi? = null,
    @SerializedName("id") val id: Int? = null
)

