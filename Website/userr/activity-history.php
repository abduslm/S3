<?php
include_once('includes/session.php');

// Filter setup
$filter_tanggal = isset($_GET['tanggal']) ? $_GET['tanggal'] : '';
$filter_aktivitas = isset($_GET['aktivitas']) ? $_GET['aktivitas'] : '';
$filter_user = isset($_GET['user']) ? $_GET['user'] : '';

// Get unique activities for filter dropdown dari riwayat_aktivitas
$activities_query = "SELECT DISTINCT aktivitas FROM riwayat_aktivitas ORDER BY aktivitas";
$activities_result = $mysqli->query($activities_query);

// Query untuk summary cards dengan filter
$summary_query = "SELECT ra.*, 
                um.Nama as nama_mobile, um.username as username_mobile,
                uw.Nama as nama_web, uw.username as username_web
        FROM riwayat_aktivitas ra
        LEFT JOIN user_mobile um ON ra.id_userMobile = um.id_userMobile
        LEFT JOIN user_web uw ON ra.id_userWeb = uw.id_userWeb
        WHERE 1=1";

if (!empty($filter_tanggal)) {
    $summary_query .= " AND ra.tanggal = '" . $mysqli->real_escape_string($filter_tanggal) . "'";
}
if (!empty($filter_aktivitas)) {
    $summary_query .= " AND ra.aktivitas LIKE '%" . $mysqli->real_escape_string($filter_aktivitas) . "%'";
}
if (!empty($filter_user)) {
    $summary_query .= " AND (um.Nama LIKE '%" . $mysqli->real_escape_string($filter_user) . "%' 
                    OR uw.Nama LIKE '%" . $mysqli->real_escape_string($filter_user) . "%'
                    OR um.username LIKE '%" . $mysqli->real_escape_string($filter_user) . "%'
                    OR uw.username LIKE '%" . $mysqli->real_escape_string($filter_user) . "%')";
}

// Total activities
$total_result = $mysqli->query($summary_query);
$total_count = $total_result->num_rows;

// Mobile activities (filter khusus untuk mobile)
$mobile_query = $summary_query . " AND ra.id_userMobile IS NOT NULL";
$mobile_result = $mysqli->query($mobile_query);
$mobile_count = $mobile_result->num_rows;

// Web activities (filter khusus untuk web)
$web_query = $summary_query . " AND ra.id_userWeb IS NOT NULL";
$web_result = $mysqli->query($web_query);
$web_count = $web_result->num_rows;

// Today's activities
$today_query = "SELECT COUNT(*) as cnt FROM riwayat_aktivitas 
        WHERE tanggal = CURDATE() 
        " . (!empty($filter_aktivitas) ? "AND aktivitas LIKE '%" . $mysqli->real_escape_string($filter_aktivitas) . "%'" : "");
$today_count = $mysqli->query($today_query)->fetch_assoc()['cnt'];
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
                                                    <?php 
                                                    $activities_result = $mysqli->query($activities_query);
                                                    while($activity = $activities_result->fetch_assoc()): 
                                                    ?>
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
                                                    <!-- Tombol Filter -->
                                                    <button type="submit" class="btn btn-primary">
                                                        <i class="fa fa-search"></i> Filter
                                                    </button>
                                                    
                                                    <!-- Tombol Reset -->
                                                    <button type="button" class="btn btn-default" id="resetFilter">
                                                        <i class="fa fa-refresh"></i> Reset
                                                    </button>
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
                                <span class="info-box-number"><?php echo number_format($total_count); ?></span>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3 col-sm-6 col-xs-12">
                        <div class="info-box">
                            <span class="info-box-icon bg-green"><i class="fa fa-users"></i></span>
                            <div class="info-box-content">
                                <span class="info-box-text">Aktivitas Mobile</span>
                                <span class="info-box-number">
                                    <?php echo number_format($mobile_count); ?>
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
                                    <?php echo number_format($web_count); ?>
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
                                    <?php echo number_format($today_count); ?>
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
                                <div class="box-tools">
                                    <div class="input-group" style="width: 200px;">
                                        <input type="text" name="table_search" id="table_search" class="form-control input-sm pull-right" placeholder="Search dalam tabel...">
                                        <div class="input-group-btn">
                                            <button class="btn btn-sm btn-default"><i class="fa fa-search"></i></button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="box-body">
                                <div class="table-responsive">
                                    <table class="table table-striped table-bordered table-hover" id="activity-datatable">
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
                                            <!-- Data akan di-load oleh DataTables via AJAX -->
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- ... kode sebelumnya tetap sama ... -->

<script>
$(document).ready(function() {
    // Inisialisasi DataTable
    var table = $('#activity-datatable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "includes/get-activities.php",
            "type": "POST",
            "data": function(d) {
                // Kirim parameter filter ke server
                d.tanggal = $('#tanggal').val();
                d.aktivitas = $('#aktivitas').val();
                d.user = $('#user').val();
            }
        },
        "pageLength": 10,
        "lengthMenu": [[10, 25, 50, 100, -1], [10, 25, 50, 100, "All"]],
        "order": [[1, 'desc']], // Urutkan berdasarkan kolom tanggal_waktu
        "columns": [
            { 
                "data": null,
                "render": function(data, type, row, meta) {
                    return meta.row + meta.settings._iDisplayStart + 1;
                },
                "orderable": false
            },
            { 
                "data": "tanggal_waktu",
                "type": "date", // Tipe khusus untuk sorting tanggal
                "render": function(data, type, row) {
                    // Format untuk tampilan
                    if (type === 'display' || type === 'filter') {
                        // Pisahkan tanggal dan waktu
                        var parts = data.split(' ');
                        var dateParts = parts[0].split('-');
                        var timeParts = parts[1].split(':');
                        
                        // Format tanggal: DD/MM/YYYY
                        var formattedDate = dateParts[2] + '/' + dateParts[1] + '/' + dateParts[0];
                        // Format waktu: HH:MM:SS
                        var formattedTime = timeParts[0] + ':' + timeParts[1] + ':' + timeParts[2];
                        
                        return '<strong>' + formattedDate + '</strong><br>' +
                               '<small class="text-muted">' + formattedTime + ' WIB</small>';
                    }
                    
                    // Untuk sorting, kembalikan data asli
                    return data;
                }
            },
            { 
                "data": "aktivitas",
                "render": function(data, type, row) {
                    var activityClass = '';
                    var activityText = data;
                    
                    // Klasifikasi berdasarkan jenis aktivitas
                    if (data.includes('LOGIN')) {
                        activityClass = 'success';
                    } else if (data.includes('LOGOUT')) {
                        activityClass = 'primary';
                    } else if (data.includes('DELETE') || data.includes('HAPUS')) {
                        activityClass = 'danger';
                    } else if (data.includes('UPDATE')) {
                        activityClass = 'warning';
                    } else if (data.includes('INSERT') || data.includes('TAMBAH') || data.includes('BUAT')) {
                        activityClass = 'info';
                    } else {
                        activityClass = 'default';
                    }
                    
                    return '<span class="label label-' + activityClass + '">' + activityText + '</span>';
                }
            },
            { 
                "data": "keterangan",
                "render": function(data, type, row) {
                    return data || '-';
                }
            },
            { 
                "data": "user_name",
                "render": function(data, type, row) {
                    return data || 'System';
                }
            },
            { 
                "data": "platform",
                "render": function(data, type, row) {
                    if (data === 'Mobile') {
                        return '<span class="label label-success">Mobile</span>';
                    } else if (data === 'Web') {
                        return '<span class="label label-primary">Web</span>';
                    } else {
                        return '<span class="label label-default">System</span>';
                    }
                }
            }
        ],
        "language": {
            "processing": "Memproses...",
            "lengthMenu": "Tampilkan _MENU_ entri",
            "zeroRecords": "Tidak ada data yang ditemukan",
            "info": "Menampilkan _START_ sampai _END_ dari _TOTAL_ entri",
            "infoEmpty": "Menampilkan 0 sampai 0 dari 0 entri",
            "infoFiltered": "(disaring dari _MAX_ total entri)",
            "search": "Cari:",
            "paginate": {
                "first": "Pertama",
                "last": "Terakhir",
                "next": "Berikutnya",
                "previous": "Sebelumnya"
            }
        },
        "responsive": true,
        "dom": '<"row"<"col-sm-6"l><"col-sm-6"f>>' +
               '<"row"<"col-sm-12"tr>>' +
               '<"row"<"col-sm-5"i><"col-sm-7"p>>',
        "createdRow": function(row, data, dataIndex) {
            // Tambahkan class berdasarkan jenis aktivitas untuk styling row
            if (data.aktivitas.includes('LOGIN')) {
                $(row).addClass('success');
            } else if (data.aktivitas.includes('DELETE') || data.aktivitas.includes('HAPUS')) {
                $(row).addClass('danger');
            } else if (data.aktivitas.includes('UPDATE')) {
                $(row).addClass('warning');
            } else if (data.aktivitas.includes('INSERT') || data.aktivitas.includes('TAMBAH')) {
                $(row).addClass('info');
            }
        }
    });

    // Search box untuk tabel
    $('#table_search').on('keyup', function() {
        table.search(this.value).draw();
    });

    // Submit filter form
    $('#filterForm').on('submit', function(e) {
        e.preventDefault();
        
        // Reload DataTable dengan filter baru
        table.ajax.reload();
        
        // Update summary cards via AJAX
        updateSummaryCards();
        
        return false;
    });

    // Tombol reset
    $('#resetFilter').on('click', function(e) {
        e.preventDefault();
        
        // Clear form
        $('#tanggal').val('');
        $('#aktivitas').val('');
        $('#user').val('');
        
        // Reload DataTable tanpa filter
        table.ajax.reload();
        
        // Update summary cards
        updateSummaryCards();
        
        return false;
    });

    // Fungsi untuk update summary cards
    function updateSummaryCards() {
        $.ajax({
            url: 'includes/get-activity-summary.php',
            type: 'POST',
            data: {
                tanggal: $('#tanggal').val(),
                aktivitas: $('#aktivitas').val(),
                user: $('#user').val()
            },
            success: function(data) {
                try {
                    var summary = JSON.parse(data);
                    $('.info-box-number').eq(0).text(summary.total);
                    $('.info-box-number').eq(1).text(summary.mobile);
                    $('.info-box-number').eq(2).text(summary.web);
                    $('.info-box-number').eq(3).text(summary.today);
                } catch(e) {
                    console.error('Error parsing summary data:', e);
                }
            },
            error: function(xhr, status, error) {
                console.error('Error updating summary cards:', error);
            }
        });
    }
    
    // Auto-refresh tabel setiap 30 detik (opsional)
    setInterval(function() {
        table.ajax.reload(null, false); // false = tidak reset paging
    }, 30000);
});
</script>

<style>
/* Pastikan tabel memiliki width 100% */
#activity-datatable {
    width: 100% !important;
}

