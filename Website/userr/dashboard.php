<?php

include_once("includes/config.php");

// =======================================================
// MENGAMBIL DATA DARI DATABASE
// =======================================================

// Data Admin dari tabel user_mobile
$query_admin = "SELECT COUNT(*) as count FROM `user_mobile` ";
$result_admin = mysqli_query($mysqli, $query_admin);
$admin_data = mysqli_fetch_assoc($result_admin);

$query_admin = "SELECT COUNT(*) as count FROM `user_mobile` where status='online'";
$result_admin = mysqli_query($mysqli, $query_admin);
$admin_on_data = mysqli_fetch_assoc($result_admin);

// Data Visitor dari tabel visitors
$query_visitor = "SELECT COUNT(*) as count FROM `visitors`";
$result_visitor = mysqli_query($mysqli, $query_visitor);
$visitor_data = mysqli_fetch_assoc($result_visitor);

$user_counts_mini = [
    'admin'   => [
        'count' => $admin_data['count'], 
        'desc' => 'Total Admin Mobile', 
        'color' => 'blue'
    ],
    
    'admin-on'   => [
        'count' => $admin_on_data['count'], 
        'desc' => 'Admin Mobile Online', 
        'color' => 'purple'
    ],

    'visitor' => [
        'count' => $visitor_data['count'], 
        'desc' => 'Total Pengunjung Web', 
        'color' => 'red'
    ], 
];
// =======================================================
?>

<section class="content-header">
    <h1>
        Dashboard
        <small>Ringkasan Akses Pengguna</small>
    </h1>
</section>

<section class="content">

    <div class="row">

        <?php 
        foreach ($user_counts_mini as $role => $data): 
        ?>
        <div class="col-lg-3 col-md-6 col-sm-6 col-xs-12">
            <div class="small-box role-card role-<?php echo $data['color']; ?>">
                <div class="inner">
                    <h3 class="role-title">
                        <?php 
                        switch ($role) {
                            case 'visitor':
                                echo 'Pengunjung Web';
                                break;
                            case 'admin':
                                echo 'Admin Mobile';
                                break;
                            case 'admin-on':
                                echo 'ONLINE';
                                break;
                            default:
                                echo ucfirst($role);
                        }
                        ?> 
                        <span class="role-icon pull-right">
                            <?php 
                            // Menentukan Ikon Card
                            switch ($role) {
                                case 'visitor':
                                    echo '<i class="fa-solid fa-users"></i>';
                                    break;
                                case 'admin':
                                    echo '<i class="fa-solid fa-shield-alt"></i>';
                                    break;
                                case 'admin-on':
                                    echo '<i class="fas fa-user fa-2x"></i>
                                        <i class="fas fa-circle text-success" 
                                        style="position: absolute; top: 20px; right: 22px; font-size: 0.7em;"></i>
                                        ';
                                    break;
                                default:
                                    echo '<i class="fa-solid fa-user"></i>';
                            }
                            ?>
                        </span>
                    </h3>
                    <div class="role-content">
                        <?php if ($role === 'visitor'): ?>
                            <p class="role-count role-count-lg"><?php echo number_format($data['count']); ?></p>
                        <?php else: ?>
                            <p class="role-count"><?php echo $data['count']; ?> User(s)</p>
                        <?php endif; ?>
                        
                        <p class="role-desc"><?php echo $data['desc']; ?></p>
                    </div>
                </div>
            </div>
        </div>
        <?php endforeach; ?>
        
    </div>
    
    <div class="row">
        <div class="col-xs-12">
            
            <div class="box box-primary">
                <div class="box-header with-border">
                    <h3 class="box-title">🌎 Traffic Source Detail</h3>
                </div>
                <div class="box-body">
                    <div class="table-responsive"> 
                        
                    <?php
                    // Query untuk mendapatkan data traffic source
                    $query = "SELECT 
                                browser,
                                country,
                                city,
                                COUNT(*) as session_count,
                                COUNT(*) * 100.0 / (SELECT COUNT(*) FROM visitors) as percentage
                              FROM visitors 
                              WHERE country IS NOT NULL AND country != ''
                              GROUP BY browser, country, city
                              ORDER BY session_count DESC
                              LIMIT 10";
                    
                    $result = mysqli_query($mysqli, $query);
                    
                    // Hitung total sessions untuk persentase yang lebih akurat
                    $total_query = "SELECT COUNT(*) as total FROM visitors";
                    $total_result = mysqli_query($mysqli, $total_query);
                    $total_data = mysqli_fetch_assoc($total_result);
                    $total_sessions = $total_data['total'];
                    
                    if (mysqli_num_rows($result) > 0):
                    ?>
                        <table class="table table-striped table-bordered user-list-table dataTable">
                            <thead>
                                <tr>
                                    <th>Browser</th>
                                    <th>Negara</th>
                                    <th>Kota</th>
                                    <th>Sesi</th>
                                    <th>Persentase</th>
                                </tr>
                            </thead>
                            <tbody>
                                <?php 
                            $counter = 0;
                            $label_colors = ['bg-red', 'bg-yellow', 'bg-blue', 'bg-green', 'bg-purple', 'bg-orange', 'bg-teal', 'bg-pink', 'bg-indigo', 'bg-cyan'];
                            
                            while ($row = mysqli_fetch_assoc($result)): 
                                $counter++;
                                $color_index = ($counter - 1) % count($label_colors);
                                $color_class = $label_colors[$color_index];
                                
                                // Format persentase
                                $percentage = ($row['session_count'] / $total_sessions) * 100;
                                $formatted_percentage = number_format($percentage, 1) . '%';
                                
                                // Dapatkan kode negara untuk flag icon
                                $country_code = strtolower(getCountryCode($row['country']));
                            ?>

                               <tr>
                                <td>
                                    <span class="label <?php echo $color_class; ?> custom-label">
                                        <?php echo htmlspecialchars($row['browser'] ?: 'Unknown'); ?>
                                    </span>
                                </td>
                                <td>
                                    <?php echo htmlspecialchars($row['country'] ?: 'Unknown'); ?> 
                                    <?php if($country_code): ?>
                                        <span class="flag-icon flag-icon-<?php echo $country_code; ?>"></span>
                                    <?php endif; ?>
                                </td>
                                <td><?php echo htmlspecialchars($row['city'] ?: 'Unknown'); ?></td>
                                <td><?php echo number_format($row['session_count']); ?></td>
                                <td><?php echo $formatted_percentage; ?></td>
                            </tr>
                            <?php endwhile; ?>
                            </tbody>
                        </table>
                        
                        <?php else: ?>
                        <div class="alert alert-info">
                            <p>Tidak ada data traffic source yang tersedia.</p>
                        </div>
                        <?php endif; ?>

                    </div>
                    </div>
                </div>
            </div>
        </div>
    <script>
        // Inisialisasi DataTables
        $(document).ready(function() {
            $('.user-list-table').DataTable({
                'paging'      : true,
                'lengthChange': false,
                'searching'   : true,
                'ordering'    : true,
                'info'        : true,
                'autoWidth'   : false
                'order'       : [[3, 'desc']] 
            });
        });
    </script>
    
    <?php
// Fungsi untuk mendapatkan kode negara dari nama negara
function getCountryCode($country_name) {
    $country_codes = [
        'indonesia' => 'id',
        'malaysia' => 'my',
        'thailand' => 'th',
        'singapore' => 'sg',
        'vietnam' => 'vn',
        'philippines' => 'ph',
        'united states' => 'us',
        'united kingdom' => 'gb',
        'australia' => 'au',
        'japan' => 'jp',
        'south korea' => 'kr',
        'china' => 'cn',
        'india' => 'in',
        'germany' => 'de',
        'france' => 'fr',
        'italy' => 'it',
        'spain' => 'es',
        'brazil' => 'br',
        'canada' => 'ca',
        'russia' => 'ru',
        'mexico' => 'mx',
    ];
    
    $country_lower = strtolower(trim($country_name));
    return isset($country_codes[$country_lower]) ? $country_codes[$country_lower] : '';
}
?>

</section>
<?php
    include('includes/footer.php');
?>