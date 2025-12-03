<?php
include('includes/session.php');

// Pagination setup
$records_per_page = 10;
$page = isset($_GET['page']) ? (int)$_GET['page'] : 1;
$start_from = ($page - 1) * $records_per_page;

// Filter setup
$filter_tanggal = isset($_GET['tanggal']) ? $_GET['tanggal'] : '';
$filter_aktivitas = isset($_GET['aktivitas']) ? $_GET['aktivitas'] : '';
$filter_user = isset($_GET['user']) ? $_GET['user'] : '';

// Build query dengan filter
$query = "SELECT ra.*, 
                um.Nama as nama_mobile, um.username as username_mobile,
                uw.Nama as nama_web, uw.username as username_web
        FROM riwayat_aktivitas ra
        LEFT JOIN user_mobile um ON ra.id_userMobile = um.id_userMobile
        LEFT JOIN user_web uw ON ra.id_userWeb = uw.id_userWeb
        WHERE 1=1";

$query_count = "SELECT COUNT(*) as total 
                FROM riwayat_aktivitas ra
                LEFT JOIN user_mobile um ON ra.id_userMobile = um.id_userMobile
                LEFT JOIN user_web uw ON ra.id_userWeb = uw.id_userWeb
                WHERE 1=1";

// Apply filters
if (!empty($filter_tanggal)) {
    $query .= " AND ra.tanggal = '" . $mysqli->real_escape_string($filter_tanggal) . "'";
    $query_count .= " AND ra.tanggal = '" . $mysqli->real_escape_string($filter_tanggal) . "'";
}

if (!empty($filter_aktivitas)) {
    $query .= " AND ra.aktivitas LIKE '%" . $mysqli->real_escape_string($filter_aktivitas) . "%'";
    $query_count .= " AND ra.aktivitas LIKE '%" . $mysqli->real_escape_string($filter_aktivitas) . "%'";
}

if (!empty($filter_user)) {
    $query .= " AND (um.Nama LIKE '%" . $mysqli->real_escape_string($filter_user) . "%' 
                    OR uw.Nama LIKE '%" . $mysqli->real_escape_string($filter_user) . "%'
                    OR um.username LIKE '%" . $mysqli->real_escape_string($filter_user) . "%'
                    OR uw.username LIKE '%" . $mysqli->real_escape_string($filter_user) . "%')";
    $query_count .= " AND (um.Nama LIKE '%" . $mysqli->real_escape_string($filter_user) . "%' 
                    OR uw.Nama LIKE '%" . $mysqli->real_escape_string($filter_user) . "%'
                    OR um.username LIKE '%" . $mysqli->real_escape_string($filter_user) . "%'
                    OR uw.username LIKE '%" . $mysqli->real_escape_string($filter_user) . "%')";
}

// Order dan limit
$query .= " ORDER BY ra.tanggal DESC, ra.jam DESC 
            LIMIT $start_from, $records_per_page";

// Execute queries
$result = $mysqli->query($query);
$count_result = $mysqli->query($query_count);
$total_records = $count_result->fetch_assoc()['total'];
$total_pages = ceil($total_records / $records_per_page);

// Get unique activities for filter dropdown
$activities_query = "SELECT DISTINCT aktivitas FROM riwayat_aktivitas ORDER BY aktivitas";
$activities_result = $mysqli->query($activities_query);
?>

<div class="row">
    <div class="col-xs-12">
        <div class="box box-primary">
            <div class="box-header with-border">
                <h3 class="box-title">Riwayat Aktivitas</h3>
            </div>
            
            <div class="box-body">
                <!-- Filter Section -->
                <div class="row">
                    <div class="col-md-12">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <h4 class="panel-title">Filter Riwayat</h4>
                            </div>
                            <div class="panel-body">
                                <form method="GET" id="filterForm">
                                    <input type="hidden" name="page" value="activity-history">
                                    
                                    <div class="row">
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="tanggal">Tanggal</label>
                                                <input type="date" class="form-control" id="tanggal" name="tanggal" 
                                                    value="<?php echo htmlspecialchars($filter_tanggal); ?>">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="aktivitas">Jenis Aktivitas</label>
                                                <select class="form-control" id="aktivitas" name="aktivitas">
                                                    <option value="">Semua Aktivitas</option>
                                                    <?php while($activity = $activities_result->fetch_assoc()): ?>
                                                        <option value="<?php echo htmlspecialchars($activity['aktivitas']); ?>" 
                                                            <?php echo ($filter_aktivitas == $activity['aktivitas']) ? 'selected' : ''; ?>>
                                                            <?php echo htmlspecialchars($activity['aktivitas']); ?>
                                                        </option>
                                                    <?php endwhile; ?>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="user">User</label>
                                                <input type="text" class="form-control" id="user" name="user" 
                                                    placeholder="Cari berdasarkan nama/user" 
                                                    value="<?php echo htmlspecialchars($filter_user); ?>">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label>&nbsp;</label>
                                                <div>
                                                    <button type="submit" class="btn btn-primary">
                                                        <i class="fa fa-search"></i> Filter
                                                    </button>
                                                    <a href="?page=activity-history" class="btn btn-default">
                                                        <i class="fa fa-refresh"></i> Reset
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Summary Cards -->
                <div class="row">
                    <div class="col-md-3 col-sm-6 col-xs-12">
                        <div class="info-box">
                            <span class="info-box-icon bg-aqua"><i class="fa fa-history"></i></span>
                            <div class="info-box-content">
                                <span class="info-box-text">Total Aktivitas</span>
                                <span class="info-box-number"><?php echo number_format($total_records); ?></span>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3 col-sm-6 col-xs-12">
                        <div class="info-box">
                            <span class="info-box-icon bg-green"><i class="fa fa-users"></i></span>
                            <div class="info-box-content">
                                <span class="info-box-text">Aktivitas Mobile</span>
                                <span class="info-box-number">
                                    <?php 
                                    $mobile_count = $mysqli->query("SELECT COUNT(*) as cnt FROM riwayat_aktivitas WHERE id_userMobile IS NOT NULL")->fetch_assoc()['cnt'];
                                    echo number_format($mobile_count);
                                    ?>
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3 col-sm-6 col-xs-12">
                        <div class="info-box">
                            <span class="info-box-icon bg-yellow"><i class="fa fa-desktop"></i></span>
                            <div class="info-box-content">
                                <span class="info-box-text">Aktivitas Web</span>
                                <span class="info-box-number">
                                    <?php 
                                    $web_count = $mysqli->query("SELECT COUNT(*) as cnt FROM riwayat_aktivitas WHERE id_userWeb IS NOT NULL")->fetch_assoc()['cnt'];
                                    echo number_format($web_count);
                                    ?>
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3 col-sm-6 col-xs-12">
                        <div class="info-box">
                            <span class="info-box-icon bg-red"><i class="fa fa-calendar"></i></span>
                            <div class="info-box-content">
                                <span class="info-box-text">Hari Ini</span>
                                <span class="info-box-number">
                                    <?php 
                                    $today_count = $mysqli->query("SELECT COUNT(*) as cnt FROM riwayat_aktivitas WHERE tanggal = CURDATE()")->fetch_assoc()['cnt'];
                                    echo number_format($today_count);
                                    ?>
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Activity Table -->
                <div class="row">
                    <div class="col-xs-12">
                        <div class="box">
                            <div class="box-header">
                                <h3 class="box-title">Daftar Riwayat Aktivitas</h3>
                            </div>
                            <div class="box-body">
                                <?php if ($result->num_rows > 0): ?>
                                    <div class="table-responsive">
                                        <table class="table table-striped table-bordered table-hover" id="activity-table">
                                            <thead>
                                                <tr>
                                                    <th>#</th>
                                                    <th>Tanggal & Waktu</th>
                                                    <th>Aktivitas</th>
                                                    <th>Keterangan</th>
                                                    <th>User</th>
                                                    <th>Platform</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <?php 
                                                $counter = $start_from + 1;
                                                while ($row = $result->fetch_assoc()): 
                                                    $user_name = '';
                                                    $platform = '';
                                                    
                                                    if (!empty($row['nama_mobile'])) {
                                                        $user_name = $row['nama_mobile'] . ' (' . $row['username_mobile'] . ')';
                                                        $platform = '<span class="label label-success">Mobile</span>';
                                                    } elseif (!empty($row['nama_web'])) {
                                                        $user_name = $row['nama_web'] . ' (' . $row['username_web'] . ')';
                                                        $platform = '<span class="label label-primary">Web</span>';
                                                    } else {
                                                        $user_name = 'System';
                                                        $platform = '<span class="label label-default">System</span>';
                                                    }
                                                    
                                                    // Style berdasarkan jenis aktivitas
                                                    $activity_class = '';
                                                    if (strpos($row['aktivitas'], 'LOGIN') !== false) {
                                                        $activity_class = 'success';
                                                    } elseif (strpos($row['aktivitas'], 'DELETE') !== false || strpos($row['aktivitas'], 'HAPUS') !== false) {
                                                        $activity_class = 'danger';
                                                    } elseif (strpos($row['aktivitas'], 'UPDATE') !== false) {
                                                        $activity_class = 'warning';
                                                    } elseif (strpos($row['aktivitas'], 'TAMBAH') !== false || strpos($row['aktivitas'], 'BUAT') !== false) {
                                                        $activity_class = 'info';
                                                    }
                                                ?>
                                                <tr class="<?php echo $activity_class; ?>">
                                                    <td><?php echo $counter++; ?></td>
                                                    <td>
                                                        <strong><?php echo date('d/m/Y', strtotime($row['tanggal'])); ?></strong><br>
                                                        <small class="text-muted"><?php echo $row['jam']; ?></small>
                                                    </td>
                                                    <td>
                                                        <span class="label label-<?php echo $activity_class ?: 'default'; ?>">
                                                            <?php echo htmlspecialchars($row['aktivitas']); ?>
                                                        </span>
                                                    </td>
                                                    <td><?php echo htmlspecialchars($row['keterangan']); ?></td>
                                                    <td><?php echo $user_name; ?></td>
                                                    <td><?php echo $platform; ?></td>
                                                </tr>
                                                <?php endwhile; ?>
                                            </tbody>
                                        </table>
                                    </div>

                                    <!-- Pagination -->
                                    <div class="text-center">
                                        <ul class="pagination">
                                            <?php if ($page > 1): ?>
                                                <li><a href="?page=activity-history&tanggal=<?php echo $filter_tanggal; ?>&aktivitas=<?php echo $filter_aktivitas; ?>&user=<?php echo $filter_user; ?>&page=<?php echo $page - 1; ?>">«</a></li>
                                            <?php endif; ?>

                                            <?php for ($i = 1; $i <= $total_pages; $i++): ?>
                                                <li class="<?php echo ($i == $page) ? 'active' : ''; ?>">
                                                    <a href="?page=activity-history&tanggal=<?php echo $filter_tanggal; ?>&aktivitas=<?php echo $filter_aktivitas; ?>&user=<?php echo $filter_user; ?>&page=<?php echo $i; ?>">
                                                        <?php echo $i; ?>
                                                    </a>
                                                </li>
                                            <?php endfor; ?>

                                            <?php if ($page < $total_pages): ?>
                                                <li><a href="?page=activity-history&tanggal=<?php echo $filter_tanggal; ?>&aktivitas=<?php echo $filter_aktivitas; ?>&user=<?php echo $filter_user; ?>&page=<?php echo $page + 1; ?>">»</a></li>
                                            <?php endif; ?>
                                        </ul>
                                        <p class="text-muted">
                                            Menampilkan <?php echo ($start_from + 1); ?> - <?php echo min($start_from + $records_per_page, $total_records); ?> dari <?php echo number_format($total_records); ?> aktivitas
                                        </p>
                                    </div>

                                <?php else: ?>
                                    <div class="alert alert-info text-center">
                                        <i class="fa fa-info-circle"></i> Tidak ada riwayat aktivitas yang ditemukan.
                                    </div>
                                <?php endif; ?>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>

// Auto refresh every 30 seconds jika di halaman pertama
<?php if ($page == 1): ?>
setTimeout(function() {
    location.reload();
}, 30000);
<?php endif; ?>

// Initialize DataTables
$(document).ready(function() {
    $('#activity-table').DataTable({
        "paging": false,
        "searching": false,
        "ordering": true,
        "info": false,
        "autoWidth": false,
        "order": [[1, 'desc']] // Order by date descending
    });
});
</script>