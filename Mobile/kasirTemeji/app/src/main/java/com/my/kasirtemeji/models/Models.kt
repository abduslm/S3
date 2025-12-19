package com.my.kasirtemeji.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

// ============================================
// REQUEST MODELS
// ============================================

data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

// ============================================
// RESPONSE MODELS
// ============================================

data class LoginResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("data") val data: LoginData?
)

data class LoginData(
    @SerializedName("token") val token: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("user") val user: User
)

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("level") val level: String,
    @SerializedName("nama") val nama: String,
    @SerializedName("email") val email: String,
    @SerializedName("noHp") val noHp: String,
    @SerializedName("status") val status: String
)

data class LogoutResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("data") val data: LogoutData?
)

data class LogoutData(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("username") val username: String
)

// ============================================
// MODELS UNTUK USER MANAGEMENT
// ============================================

/**
 * Model untuk request create/update user
 */
data class UserRequest(
    @SerializedName("nama") val nama: String,
    @SerializedName("email") val email: String,
    @SerializedName("noHp") val noHp: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String? = null,
    @SerializedName("level") val level: String = "Kasir"
)

/**
 * Model untuk response user operations
 */
data class UserResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("data") val data: UserOperationData?
)

/**
 * Data dari response user operations
 */
data class UserOperationData(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("nama") val nama: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("level") val level: String? = null,
    @SerializedName("users") val users: List<UserItem>? = null
)

/**
 * Item user untuk list users
 */
data class UserItem(
    @SerializedName("id_userMobile") val id: Int,
    @SerializedName("Nama") val nama: String,
    @SerializedName("Email") val email: String,
    @SerializedName("NoHp") val noHp: String,
    @SerializedName("username") val username: String,
    @SerializedName("level") val level: String,
    @SerializedName("Status") val status: String
)

// ============================================
// MODELS UNTUK MENU MANAGEMENT
// ============================================

/**
 * Model untuk menu item (response)
 */
data class MenuItem(
    @SerializedName("id_menu") val id: Int,
    @SerializedName("nama_menu") val namaMenu: String,
    @SerializedName("kategori") val kategori: String,
    @SerializedName("stok") val stok: Int,
    @SerializedName("harga") val harga: Double,
    @SerializedName("status") val status: String,
    @SerializedName("gambar_base64") val gambarBase64: String? = null // Gambar dalam format base64
) : Serializable

/**
 * Model untuk request create/update menu (JSON)
 */
data class MenuRequest(
    @SerializedName("nama_menu") val namaMenu: String,
    @SerializedName("kategori") val kategori: String,
    @SerializedName("stok") val stok: Int,
    @SerializedName("harga") val harga: Double,
    @SerializedName("status") val status: String = "Tersedia",
    @SerializedName("gambar_base64") val gambarBase64: String? = null // Untuk upload via JSON
)

/**
 * Model untuk response menu operations
 */
data class MenuResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("data") val data: MenuOperationData?
)

/**
 * Data dari response menu operations
 */
data class MenuOperationData(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("nama_menu") val namaMenu: String? = null,
    @SerializedName("kategori") val kategori: String? = null,
    @SerializedName("harga") val harga: Double? = null,
    @SerializedName("stok") val stok: Int? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("menus") val menus: List<MenuItem>? = null,
    @SerializedName("menu") val menu: MenuItem? = null
)

// ============================================
// MODELS UNTUK MULTIPART FORM DATA (UPLOAD GAMBAR)
// ============================================

/**
 * Data class untuk upload gambar multipart
 */
data class MenuMultipartRequest(
    val nama_menu: String,
    val kategori: String,
    val stok: Int,
    val harga: Double,
    val status: String = "Tersedia",
    val gambar: okhttp3.MultipartBody.Part? = null
)

/**
 * Data class untuk response upload multipart
 */
data class MenuMultipartResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("data") val data: MenuSimpleData?
)

data class MenuSimpleData(
    @SerializedName("id") val id: Int,
    @SerializedName("nama_menu") val namaMenu: String,
    @SerializedName("kategori") val kategori: String,
    @SerializedName("harga") val harga: Double,
    @SerializedName("stok") val stok: Int,
    @SerializedName("status") val status: String
)
// ============================================
// MODELS UNTUK PESANAN/ORDER
// ============================================

/**
 * Item untuk detail pesanan (request)
 */
data class OrderItemRequest(
    @SerializedName("id_menu") val idMenu: Int,
    @SerializedName("jumlah") val jumlah: Int
)

/**
 * Model untuk request create order
 */
data class OrderRequest(
    @SerializedName("items") val items: List<OrderItemRequest>,
    @SerializedName("status_pesanan") val statusPesanan: String = "Pending"
)

/**
 * Model untuk detail item dalam pesanan (response) - Update
 */
data class OrderDetailItem(
    @SerializedName("id_detail") val idDetail: Int,
    @SerializedName("id_menu") val idMenu: Int,
    @SerializedName("nama_menu") val namaMenu: String,
    @SerializedName("jumlah") val jumlah: Int,
    @SerializedName("harga") val harga: Double,
    @SerializedName("subtotal") val subtotal: Double,
    @SerializedName("catatan") val catatan: String? = null // Tambah field catatan
)

/**
 * Model untuk pesanan - Update
 */
data class Order(
    @SerializedName("id_pesanan") val id: Int,
    @SerializedName("tanggal_pesanan") val tanggalPesanan: String,
    @SerializedName("total_harga") val totalHarga: Double,
    @SerializedName("status_pesanan") val statusPesanan: String,
    @SerializedName("id_userMobile") val idUserMobile: Int,
    @SerializedName("nama_kasir") val namaKasir: String,
    @SerializedName("details") val details: List<OrderDetailItem>,
    @SerializedName("catatan_umum") val catatanUmum: String? = null // Catatan umum pesanan
)
/**
 * Model untuk response order operations
 */
data class OrderResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("data") val data: OrderOperationData?
)

/**
 * Data dari response order operations
 */
data class OrderOperationData(
    @SerializedName("id_pesanan") val idPesanan: Int? = null,
    @SerializedName("total_harga") val totalHarga: Double? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("tanggal") val tanggal: String? = null,
    @SerializedName("status_baru") val statusBaru: String? = null,
    @SerializedName("pesanan") val pesanan: List<Order>? = null
)

/**
 * Model untuk update status pesanan
 */
data class UpdateOrderStatusRequest(
    @SerializedName("status_pesanan") val statusPesanan: String
)

// ============================================
// MODELS UNTUK LAPORAN/REPORTING
// ============================================

/**
 * Model untuk laporan harian
 */
data class LaporanHarian(
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("total") val total: LaporanTotal,
    @SerializedName("status_pesanan") val statusPesanan: List<StatusPesanan>,
    @SerializedName("menu_terlaris") val menuTerlaris: List<MenuTerlaris>
)

/**
 * Total dalam laporan
 */
data class LaporanTotal(
    @SerializedName("pesanan") val pesanan: Int,
    @SerializedName("pendapatan") val pendapatan: Double,
    @SerializedName("rata_rata") val rataRata: Double
)

/**
 * Status pesanan dalam laporan
 */
data class StatusPesanan(
    @SerializedName("status_pesanan") val statusPesanan: String,
    @SerializedName("jumlah") val jumlah: Int,
    @SerializedName("total") val total: Double
)

/**
 * Menu terlaris dalam laporan
 */
data class MenuTerlaris(
    @SerializedName("nama_menu") val namaMenu: String,
    @SerializedName("total_terjual") val totalTerjual: Int,
    @SerializedName("total_pendapatan") val totalPendapatan: Double
)

/**
 * Model untuk laporan bulanan
 */
data class LaporanBulanan(
    @SerializedName("bulan") val bulan: String,
    @SerializedName("periode") val periode: Periode,
    @SerializedName("total") val total: LaporanBulananTotal,
    @SerializedName("pendapatan_harian") val pendapatanHarian: List<PendapatanHarian>
)

/**
 * Periode waktu
 */
data class Periode(
    @SerializedName("start") val start: String,
    @SerializedName("end") val end: String
)

/**
 * Total dalam laporan bulanan
 */
data class LaporanBulananTotal(
    @SerializedName("pesanan") val pesanan: Int,
    @SerializedName("pendapatan") val pendapatan: Double,
    @SerializedName("pesanan_terkecil") val pesananTerkecil: Double,
    @SerializedName("pesanan_terbesar") val pesananTerbesar: Double
)

/**
 * Pendapatan harian dalam laporan bulanan
 */
data class PendapatanHarian(
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("pesanan") val pesanan: Int,
    @SerializedName("pendapatan") val pendapatan: Double
)

/**
 * Model untuk laporan custom
 */
data class LaporanCustom(
    @SerializedName("periode") val periode: Periode,
    @SerializedName("ringkasan") val ringkasan: RingkasanLaporan,
    @SerializedName("performansi_kasir") val performansiKasir: List<PerformansiKasir>
)

/**
 * Ringkasan laporan custom
 */
data class RingkasanLaporan(
    @SerializedName("total_pesanan") val totalPesanan: Int,
    @SerializedName("total_pendapatan") val totalPendapatan: Double,
    @SerializedName("rata_rata_pesanan") val rataRataPesanan: Double,
    @SerializedName("tanggal_pertama") val tanggalPertama: String,
    @SerializedName("tanggal_terakhir") val tanggalTerakhir: String
)

/**
 * Performansi kasir dalam laporan
 */
data class PerformansiKasir(
    @SerializedName("nama") val nama: String,
    @SerializedName("total_pesanan") val totalPesanan: Int,
    @SerializedName("total_pendapatan") val totalPendapatan: Double
)

/**
 * Model untuk response laporan
 */
data class LaporanResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("data") val data: LaporanData?
)

/**
 * Data dari response laporan (bisa berbagai jenis)
 */
data class LaporanData(
    @SerializedName("tanggal") val tanggal: String? = null,
    @SerializedName("bulan") val bulan: String? = null,
    @SerializedName("periode") val periode: Periode? = null,
    @SerializedName("total") val total: Map<String, Any>? = null,
    @SerializedName("status_pesanan") val statusPesanan: List<StatusPesanan>? = null,
    @SerializedName("menu_terlaris") val menuTerlaris: List<MenuTerlaris>? = null,
    @SerializedName("pendapatan_harian") val pendapatanHarian: List<PendapatanHarian>? = null,
    @SerializedName("ringkasan") val ringkasan: RingkasanLaporan? = null,
    @SerializedName("performansi_kasir") val performansiKasir: List<PerformansiKasir>? = null
)

// ============================================
// MODELS UNTUK DASHBOARD
// ============================================

/**
 * Model untuk dashboard admin
 */
data class DashboardAdmin(
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("statistik_hari_ini") val statistikHariIni: StatistikHariIni,
    @SerializedName("statistik_bulan_ini") val statistikBulanIni: StatistikBulanIni,
    @SerializedName("status_pesanan") val statusPesanan: List<StatusDashboard>,
    @SerializedName("inventory") val inventory: InventoryDashboard
)

/**
 * Statistik hari ini
 */
data class StatistikHariIni(
    @SerializedName("total_pesanan") val totalPesanan: Int,
    @SerializedName("pendapatan") val pendapatan: Double,
    @SerializedName("rata_rata") val rataRata: Double
)

/**
 * Statistik bulan ini
 */
data class StatistikBulanIni(
    @SerializedName("total_pesanan") val totalPesanan: Int,
    @SerializedName("pendapatan") val pendapatan: Double
)

/**
 * Status pesanan di dashboard
 */
data class StatusDashboard(
    @SerializedName("status_pesanan") val statusPesanan: String,
    @SerializedName("jumlah") val jumlah: Int
)

/**
 * Inventory di dashboard
 */
data class InventoryDashboard(
    @SerializedName("hampir_habis") val hampirHabis: List<MenuInventory>,
    @SerializedName("habis") val habis: List<MenuInfo>
)

/**
 * Menu untuk inventory (dengan stok)
 */
data class MenuInventory(
    @SerializedName("nama_menu") val namaMenu: String,
    @SerializedName("stok") val stok: Int,
    @SerializedName("kategori") val kategori: String
)

/**
 * Menu info sederhana
 */
data class MenuInfo(
    @SerializedName("nama_menu") val namaMenu: String,
    @SerializedName("kategori") val kategori: String
)

/**
 * Model untuk dashboard kasir
 */
data class DashboardKasir(
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("statistik_hari_ini") val statistikHariIni: StatistikKasirHariIni,
    @SerializedName("statistik_bulan_ini") val statistikBulanIni: StatistikKasirBulanIni
)

/**
 * Statistik hari ini untuk kasir
 */
data class StatistikKasirHariIni(
    @SerializedName("total_pesanan") val totalPesanan: Int,
    @SerializedName("pendapatan") val pendapatan: Double,
    @SerializedName("rata_rata") val rataRata: Double,
    @SerializedName("pending") val pending: Int
)

/**
 * Statistik bulan ini untuk kasir
 */
data class StatistikKasirBulanIni(
    @SerializedName("total_pesanan") val totalPesanan: Int,
    @SerializedName("pendapatan") val pendapatan: Double
)

/**
 * Model untuk dashboard dapur
 */
data class DashboardDapur(
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("pesanan_diproses") val pesananDiproses: List<PesananDiproses>,
    @SerializedName("statistik") val statistik: List<StatusDashboard>
)

/**
 * Pesanan yang sedang diproses
 */
data class PesananDiproses(
    @SerializedName("id_pesanan") val idPesanan: Int,
    @SerializedName("tanggal_pesanan") val tanggalPesanan: String,
    @SerializedName("total_harga") val totalHarga: Double,
    @SerializedName("nama_kasir") val namaKasir: String,
    @SerializedName("total_item") val totalItem: Int,
    @SerializedName("items") val items: List<ItemDapur>
)

/**
 * Item dalam pesanan untuk dapur
 */
data class ItemDapur(
    @SerializedName("nama_menu") val namaMenu: String,
    @SerializedName("jumlah") val jumlah: Int
)

/**
 * Model untuk response dashboard
 */
data class DashboardResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("data") val data: DashboardData?
)

/**
 * Data dashboard (bisa berbagai tipe berdasarkan role)
 */
data class DashboardData(
    @SerializedName("tanggal") val tanggal: String? = null,
    @SerializedName("statistik_hari_ini") val statistikHariIni: Map<String, Any>? = null,
    @SerializedName("statistik_bulan_ini") val statistikBulanIni: Map<String, Any>? = null,
    @SerializedName("status_pesanan") val statusPesanan: List<StatusDashboard>? = null,
    @SerializedName("inventory") val inventory: Map<String, Any>? = null,
    @SerializedName("pesanan_diproses") val pesananDiproses: List<PesananDiproses>? = null,
    @SerializedName("statistik") val statistik: List<StatusDashboard>? = null
)


data class BaseResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String?
)

// ============================================
// MODELS UNTUK ERROR RESPONSE
// ============================================

/**
 * Model untuk error response dari API
 */
data class ErrorResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String
)

// ============================================
// MODELS UNTUK PAGINATION
// ============================================

/**
 * Model untuk pagination request
 */
data class PaginationRequest(
    val limit: Int = 50,
    val offset: Int = 0
)

/**
 * Model untuk filter menu
 */
data class MenuFilter(
    val kategori: String? = null,
    val status: String? = null
)

/**
 * Model untuk filter pesanan
 */
data class OrderFilter(
    val status: String? = null,
    val tanggal: String? = null,
    val limit: Int = 50,
    val offset: Int = 0
)

/**
 * Model untuk filter laporan
 */
data class ReportFilter(
    val type: String, // "harian", "bulanan", "custom"
    val date: String? = null, // Untuk harian dan bulanan
    val startDate: String? = null, // Untuk custom
    val endDate: String? = null // Untuk custom
)

// ============================================
// ENUMS DAN CONSTANTS
// ============================================

/**
 * Enum untuk level user
 */
enum class UserLevel {
    OWNER,
    SPV,
    KASIR,
    DAPUR;

    companion object {
        fun fromString(value: String): UserLevel {
            return when (value.uppercase()) {
                "OWNER" -> OWNER
                "SPV" -> SPV
                "KASIR" -> KASIR
                "DAPUR" -> DAPUR
                else -> KASIR
            }
        }
    }
}

/**
 * Enum untuk status pesanan
 */
enum class OrderStatus {
    PENDING,
    SEDANG_DIPROSES,
    SELESAI,
    DIBATALKAN;

    companion object {
        fun fromString(value: String): OrderStatus {
            return when (value.uppercase()) {
                "PENDING" -> PENDING
                "SEDANG DIPROSES" -> SEDANG_DIPROSES
                "SEDANG_DIPROSES" -> SEDANG_DIPROSES
                "SELESAI" -> SELESAI
                "DIBATALKAN" -> DIBATALKAN
                else -> PENDING
            }
        }

        fun toStringValue(status: OrderStatus): String {
            return when (status) {
                PENDING -> "Pending"
                SEDANG_DIPROSES -> "Sedang Diproses"
                SELESAI -> "Selesai"
                DIBATALKAN -> "Dibatalkan"
            }
        }
    }
}

/**
 * Enum untuk status menu
 */
enum class MenuStatus {
    TERSEDIA,
    HABIS,
    TIDAK_TERSEDIA;

    companion object {
        fun fromString(value: String): MenuStatus {
            return when (value.uppercase()) {
                "TERSEDIA" -> TERSEDIA
                "HABIS" -> HABIS
                "TIDAK TERSEDIA" -> TIDAK_TERSEDIA
                "TIDAK_TERSEDIA" -> TIDAK_TERSEDIA
                else -> TERSEDIA
            }
        }

        fun toStringValue(status: MenuStatus): String {
            return when (status) {
                TERSEDIA -> "Tersedia"
                HABIS -> "Habis"
                TIDAK_TERSEDIA -> "Tidak Tersedia"
            }
        }
    }
}

/**
 * Enum untuk kategori menu
 */
object MenuCategory {
    const val MAKANAN = "Makanan"
    const val MINUMAN = "Minuman"
    const val SIGNATURE = "Signature"
    const val LAINNYA = "Lainnya"

    val ALL_CATEGORIES = listOf(MAKANAN, MINUMAN, SIGNATURE, LAINNYA)
}

// ============================================
// DATA CLASS UNTUK UI STATE
// ============================================

/**
 * State untuk login
 */
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val data: LoginData) : LoginState()
    data class Error(val message: String) : LoginState()
}

/**
 * State untuk data user
 */
sealed class UserState {
    object Loading : UserState()
    data class Success(val users: List<UserItem>) : UserState()
    data class Error(val message: String) : UserState()
}

/**
 * State untuk data menu
 */
sealed class MenuState {
    object Loading : MenuState()
    data class Success(val menus: List<MenuItem>) : MenuState()
    data class Error(val message: String) : MenuState()
}

/**
 * State untuk data pesanan
 */
sealed class OrderState {
    object Loading : OrderState()
    data class Success(val orders: List<Order>) : OrderState()
    data class Error(val message: String) : OrderState()
}

/**
 * State untuk data dashboard
 */
sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val data: DashboardData) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

/**
 * State untuk data laporan
 */
sealed class ReportState {
    object Loading : ReportState()
    data class Success(val data: LaporanData) : ReportState()
    data class Error(val message: String) : ReportState()
}

// ============================================
// HELPER CLASSES
// ============================================

/**
 * Class untuk menyimpan selected item dalam order
 */
data class SelectedMenuItem(
    val menu: MenuItem,
    var quantity: Int = 1
) {
    val subtotal: Double get() = menu.harga * quantity

    fun increment() {
        quantity++
    }

    fun decrement() {
        if (quantity > 1) {
            quantity--
        }
    }
}

/**
 * Summary untuk order
 */
data class OrderSummary(
    val totalItems: Int,
    val totalQuantity: Int,
    val totalPrice: Double,
    val items: List<SelectedMenuItem>
)

/**
 * Filter untuk data
 */
data class DataFilter(
    val searchQuery: String = "",
    val category: String? = null,
    val status: String? = null,
    val date: String? = null,
    val sortBy: String = "id",
    val sortOrder: String = "desc"
)

/**
 * Model untuk WiFi data
 */
data class Wifi(
    @SerializedName("id_wifi") val id: Int,
    @SerializedName("nama_wifi") val namaWifi: String,
    @SerializedName("password_wifi") val password: String,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)
/**
 * Response untuk get WiFi list
 * data langsung berisi List<Wifi>
 */
data class WifiResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: List<Wifi>?,  // <-- LANGSUNG LIST!
    @SerializedName("timestamp") val timestamp: String? = null
)

/**
 * Response untuk operasi WiFi (add/update/delete)
 * data berisi object
 */
data class WifiOperationResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String?, // <-- OBJECT untuk operasi
    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("data") val data: WifiData?
)

/**
 * Data wrapper untuk operasi WiFi
 */
data class WifiData(
    @SerializedName("id_wifi") val id: Int? = null,
    @SerializedName("nama_wifi") val namaWifi: String? = null,
    @SerializedName("password_wifi") val password: String? = null,
    @SerializedName("wifi") val wifi: Wifi? = null
)

// Request tambah WiFi
data class WifiRequest(
    @SerializedName("nama_wifi") val nama_wifi: String? = null,
    @SerializedName("password_wifi") val password_wifi: String? = null
)

// Request update WiFi
data class WifiUpdateRequest(
    @SerializedName("id_wifi") val id_wifi: Int? = null,
    @SerializedName("nama_wifi") val nama_wifi: String? = null,
    @SerializedName("password_wifi") val password_wifi: String? = null
)

// Request delete WiFi
data class WifiDeleteRequest(
    @SerializedName("id") val id_wifi: Int? = null
)



// ============================================
// API RESULT WRAPPER
// ============================================

data class ApiResult<T>(
    val status: Boolean,
    val data: T? = null,
    val message: String? = null,
    val errorCode: Int? = null
) {
    companion object {
        fun <T> success(data: T): ApiResult<T> = ApiResult(true, data)
        fun <T> error(message: String, errorCode: Int? = null): ApiResult<T> =
            ApiResult(false, null, message, errorCode)
    }
}