<?php
session_start();
include_once('config.php');

// Connect to database
$mysqli = new mysqli(DATABASE_HOST, DATABASE_USER, DATABASE_PASS, DATABASE_NAME, DATABASE_PORT);

// Jika user belum login, redirect ke login page
if(!isset($_SESSION['login_username'])) {
    
    // Cek cookie remember me
    if(isset($_COOKIE['remember_token'])) {
        $token = $_COOKIE['remember_token'];
        $query = "SELECT id_userWeb, username, level FROM user_web WHERE remember_token = ? AND token_expiry > NOW()";
        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("s", $token);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if($result->num_rows > 0) {
            $user = $result->fetch_assoc();
            
            // Update status menjadi online
            $updateStatusQuery = "UPDATE user_web SET Status = 'online' WHERE id_userWeb = ?";
            $updateStatusStmt = $mysqli->prepare($updateStatusQuery);
            $updateStatusStmt->bind_param("s", $user['user_id']);
            $updateStatusStmt->execute();
            $updateStatusStmt->close();

            $_SESSION['login_username'] = $user['username'];
            $_SESSION['user_id'] = $user['user_id'];
            $_SESSION['user_level'] = $user['user_level'];

            // User sudah login via cookie, lanjutkan ke halaman
            return;
        } else {
            // Token tidak valid, hapus cookie
            setcookie('remember_token', '', time() - 3600, '/');
        }
    }
    
    // Jika tidak ada session dan cookie invalid, redirect ke login
    header("Location: index.php");
    exit();
}
?>