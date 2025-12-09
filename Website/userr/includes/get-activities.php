<?php
include_once('session.php');

// DataTables server-side processing
$requestData = $_REQUEST;

// Kolom yang bisa diurutkan
$columns = array(
    0 => 'ra.tanggal',      // Kolom 0: tanggal saja
    1 => 'ra.tanggal',      // Kolom 1: tanggal (untuk sorting utama)
    2 => 'ra.aktivitas',
    3 => 'ra.keterangan',
    4 => 'user_name',
    5 => 'platform'
);

// Build query utama
$query = "SELECT ra.*, 
                um.Nama as nama_mobile, um.username as username_mobile,
                uw.Nama as nama_web, uw.username as username_web,
                CASE 
                    WHEN ra.id_userMobile IS NOT NULL THEN 'Mobile'
                    WHEN ra.id_userWeb IS NOT NULL THEN 'Web'
                    ELSE 'System'
                END as platform,
                CASE 
                    WHEN ra.id_userMobile IS NOT NULL THEN CONCAT(um.Nama, ' (', um.username, ')')
                    WHEN ra.id_userWeb IS NOT NULL THEN CONCAT(uw.Nama, ' (', uw.username, ')')
                    ELSE 'System'
                END as user_name,
                DATE_FORMAT(ra.tanggal, '%d/%m/%Y') as tanggal_formatted,
                CONCAT(ra.tanggal, ' ', ra.jam) as tanggal_waktu_sort,
                ra.tanggal as tanggal_sort,
                ra.jam as jam_sort
        FROM riwayat_aktivitas ra
        LEFT JOIN user_mobile um ON ra.id_userMobile = um.id_userMobile
        LEFT JOIN user_web uw ON ra.id_userWeb = uw.id_userWeb
        WHERE 1=1";

// Filter dari form
$tanggal = isset($_POST['tanggal']) ? $_POST['tanggal'] : '';
$aktivitas = isset($_POST['aktivitas']) ? $_POST['aktivitas'] : '';
$user = isset($_POST['user']) ? $_POST['user'] : '';

// Apply filters
if (!empty($tanggal)) {
    $query .= " AND ra.tanggal = '" . $mysqli->real_escape_string($tanggal) . "'";
}
if (!empty($aktivitas)) {
    $query .= " AND ra.aktivitas LIKE '%" . $mysqli->real_escape_string($aktivitas) . "%'";
}
if (!empty($user)) {
    $query .= " AND (um.Nama LIKE '%" . $mysqli->real_escape_string($user) . "%' 
                    OR uw.Nama LIKE '%" . $mysqli->real_escape_string($user) . "%'
                    OR um.username LIKE '%" . $mysqli->real_escape_string($user) . "%'
                    OR uw.username LIKE '%" . $mysqli->real_escape_string($user) . "%')";
}

// Query untuk total records tanpa pagination
$result = $mysqli->query($query);
$totalData = $result->num_rows;
$totalFiltered = $totalData;

// Search dari DataTables search box
if (!empty($requestData['search']['value'])) {
    $search = $requestData['search']['value'];
    $query .= " AND (ra.aktivitas LIKE '%" . $mysqli->real_escape_string($search) . "%' 
                    OR ra.keterangan LIKE '%" . $mysqli->real_escape_string($search) . "%'
                    OR ra.tanggal LIKE '%" . $mysqli->real_escape_string($search) . "%'
                    OR ra.jam LIKE '%" . $mysqli->real_escape_string($search) . "%'
                    OR um.Nama LIKE '%" . $mysqli->real_escape_string($search) . "%'
                    OR uw.Nama LIKE '%" . $mysqli->real_escape_string($search) . "%'
                    OR um.username LIKE '%" . $mysqli->real_escape_string($search) . "%'
                    OR uw.username LIKE '%" . $mysqli->real_escape_string($search) . "%')";
    
    $result = $mysqli->query($query);
    $totalFiltered = $result->num_rows;
}

// Ordering - Default: urutkan berdasarkan tanggal DESC, jam DESC
$orderColumn = 'ra.tanggal';
$orderDir = 'DESC';
$secondaryOrder = ', ra.jam DESC';

// Jika ada request order dari DataTables
if (isset($requestData['order'][0]['column'])) {
    $orderColumn = $columns[$requestData['order'][0]['column']];
    $orderDir = $requestData['order'][0]['dir'];
    
    // Jika ordering berdasarkan tanggal, tambahkan jam sebagai secondary sort
    if ($orderColumn == 'ra.tanggal') {
        $secondaryOrder = ", ra.jam $orderDir";
    } else {
        $secondaryOrder = "";
    }
}

// Tambahkan ordering ke query
$query .= " ORDER BY $orderColumn $orderDir" . $secondaryOrder;

// Pagination
if ($requestData['length'] != -1) {
    $query .= " LIMIT " . $requestData['start'] . ", " . $requestData['length'];
}

// Debug query (opsional)
// error_log("Query: " . $query);

// Eksekusi query final
$result = $mysqli->query($query);

// Prepare data untuk JSON
$data = array();
while ($row = $result->fetch_assoc()) {
    $nestedData = array();
    // Kirim format YYYY-MM-DD HH:MM:SS untuk sorting yang benar
    $nestedData['tanggal_waktu'] = $row['tanggal'] . ' ' . $row['jam'];
    $nestedData['tanggal_formatted'] = $row['tanggal_formatted'];
    $nestedData['jam'] = $row['jam'];
    $nestedData['aktivitas'] = $row['aktivitas'];
    $nestedData['keterangan'] = $row['keterangan'];
    $nestedData['user_name'] = $row['user_name'];
    $nestedData['platform'] = $row['platform'];
    
    $data[] = $nestedData;
}

// JSON response
$json_data = array(
    "draw" => intval($requestData['draw']),
    "recordsTotal" => intval($totalData),
    "recordsFiltered" => intval($totalFiltered),
    "data" => $data
);

echo json_encode($json_data);
?>