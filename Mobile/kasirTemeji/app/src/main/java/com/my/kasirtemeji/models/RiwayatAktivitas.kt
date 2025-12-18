package com.my.kasirtemeji.models

import com.google.gson.annotations.SerializedName

// ==================== ACTIVITY LOGS ====================

data class RiwayatAktivitas(
    @SerializedName("id_riwayat") val id_riwayat: Int,
    @SerializedName("tanggal") val tanggal: String?,
    @SerializedName("jam") val jam: String?,
    @SerializedName("aktivitas") val aktivitas: String?,
    @SerializedName("keterangan") val keterangan: String?,
    @SerializedName("id_userMobile") val id_userMobile: Int?,
    @SerializedName("id_userWeb") val id_userWeb: Int?
)
