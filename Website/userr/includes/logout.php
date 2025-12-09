<?php
// logout.php
session_start();
include_once('config.php');
$mysqli = new mysqli(DATABASE_HOST, DATABASE_USER, DATABASE_PASS, DATABASE_NAME, DATABASE_PORT);

if (isset($_SESSION['login_id'])) {
    // 1. UPDATE STATUS USER MENJADI OFFLINE
    $updateStatusQuery = "UPDATE user_web SET Status = 'OFFLINE' WHERE id_userWeb = ?";
    $updateStatusStmt = $mysqli->prepare($updateStatusQuery);
    $updateStatusStmt->bind_param("s", $_SESSION['login_id']);
    $updateStatusStmt->execute();
    $updateStatusStmt->close();
    // 3. HAPUS REMEMBER TOKEN
    $query = "UPDATE user_web SET remember_token = NULL, token_expiry = NULL WHERE id_userWeb = ?";
    $stmt = $mysqli->prepare($query);
    $stmt->bind_param("s", $_SESSION['login_id']);
    $stmt->execute();
    $stmt->close();
}

// 4. HAPUS COOKIE
setcookie('remember_token', '', time() - 3600, '/');

// 5. HAPUS SEMUA DATA SESSION
$_SESSION = array();

// menghapus session cookie
if (ini_get("session.use_cookies")) {
    $params = session_get_cookie_params();
    setcookie(session_name(), '', time() - 42000,
        $params["path"], $params["domain"],
        $params["secure"], $params["httponly"]
    );
}

// 6. DESTROY SESSION
session_destroy();

// 7. REDIRECT KE HALAMAN LOGIN
header("Location: ../index.php");
exit();
?>