<?php
include_once('session.php');

$tanggal = isset($_POST['tanggal']) ? $_POST['tanggal'] : '';
$aktivitas = isset($_POST['aktivitas']) ? $_POST['aktivitas'] : '';
$user = isset($_POST['user']) ? $_POST['user'] : '';

// Build query dasar
$base_query = "FROM riwayat_aktivitas ra
        LEFT JOIN user_mobile um ON ra.id_userMobile = um.id_userMobile
        LEFT JOIN user_web uw ON ra.id_userWeb = uw.id_userWeb
        WHERE 1=1";

// Apply filters
$where_conditions = "";
if (!empty($tanggal)) {
    $where_conditions .= " AND ra.tanggal = '" . $mysqli->real_escape_string($tanggal) . "'";
}
if (!empty($aktivitas)) {
    $where_conditions .= " AND ra.aktivitas LIKE '%" . $mysqli->real_escape_string($aktivitas) . "%'";
}
if (!empty($user)) {
    $where_conditions .= " AND (um.Nama LIKE '%" . $mysqli->real_escape_string($user) . "%' 
                    OR uw.Nama LIKE '%" . $mysqli->real_escape_string($user) . "%'
                    OR um.username LIKE '%" . $mysqli->real_escape_string($user) . "%'
                    OR uw.username LIKE '%" . $mysqli->real_escape_string($user) . "%')";
}

// Total activities
$total_query = "SELECT COUNT(*) as cnt " . $base_query . $where_conditions;
$total_result = $mysqli->query($total_query);
$total = $total_result->fetch_assoc()['cnt'];

// Mobile activities
$mobile_query = "SELECT COUNT(*) as cnt " . $base_query . 
                " AND ra.id_userMobile IS NOT NULL" . $where_conditions;
$mobile_result = $mysqli->query($mobile_query);
$mobile = $mobile_result->fetch_assoc()['cnt'];

// Web activities
$web_query = "SELECT COUNT(*) as cnt " . $base_query . 
             " AND ra.id_userWeb IS NOT NULL" . $where_conditions;
$web_result = $mysqli->query($web_query);
$web = $web_result->fetch_assoc()['cnt'];

// Today's activities
$today_query = "SELECT COUNT(*) as cnt FROM riwayat_aktivitas 
        WHERE tanggal = CURDATE()";
if (!empty($aktivitas)) {
    $today_query .= " AND aktivitas LIKE '%" . $mysqli->real_escape_string($aktivitas) . "%'";
}
if (!empty($tanggal)) {
    // Jika ada filter tanggal, sesuaikan dengan hari ini
    if ($tanggal == date('Y-m-d')) {
        // Do nothing, query sudah benar
    } else {
        // Jika filter tanggal bukan hari ini, set 0
        $today = 0;
        echo json_encode(array(
            'total' => number_format($total),
            'mobile' => number_format($mobile),
            'web' => number_format($web),
            'today' => number_format(0)
        ));
        exit;
    }
}
$today_result = $mysqli->query($today_query);
$today = $today_result->fetch_assoc()['cnt'];

// Return JSON
echo json_encode(array(
    'total' => number_format($total),
    'mobile' => number_format($mobile),
    'web' => number_format($web),
    'today' => number_format($today)
));
?>