<?php
include "koneksi.php";

// Ambil IP pengunjung
$ip = $_SERVER['REMOTE_ADDR'];

// Ambil user agent
$userAgent = $_SERVER['HTTP_USER_AGENT'];

// Menentukan browser
function getBrowser($userAgent) {
    if (strpos($userAgent, 'Chrome') !== false) return "Chrome";
    if (strpos($userAgent, 'Firefox') !== false) return "Firefox";
    if (strpos($userAgent, 'Safari') !== false) return "Safari";
    if (strpos($userAgent, 'Opera') !== false) return "Opera";
    if (strpos($userAgent, 'MSIE') !== false) return "Internet Explorer";
    return "Unknown";
}

$browser = getBrowser($userAgent);

// --- Ambil Data Negara & Kota dari API --- //
$apiUrl = "http://ip-api.com/json/$ip";
$geo = json_decode(file_get_contents($apiUrl), true);

// Jika API berhasil
$country = $geo['country'] ?? "Unknown";
$city    = $geo['city'] ?? "Unknown";

// Simpan ke database
$sql = "INSERT INTO visitors (ip_address, user_agent, browser, country, city)
        VALUES ('$ip', '$userAgent', '$browser', '$country', '$city')";

mysqli_query($koneksi, $sql);
?>
`