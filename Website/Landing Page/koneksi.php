<?php
$koneksi = mysqli_connect("localhost", "root", "", "pos",3306);

if (!$koneksi) {
    die("Koneksi gagal: " . mysqli_connect_error());
}
?>