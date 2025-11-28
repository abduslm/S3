<?php

include_once("includes/config.php");

// =======================================================
// PHP SEMENTARA UNTUK DATA RINGKASAN ROLE DAN PENGUNJUNG
// =======================================================
$user_counts_mini = [
    'admin'   => ['count' => 1, 'desc' => 'Full Access', 'color' => 'red'],
    'manager' => ['count' => 1, 'desc' => 'Manage & View', 'color' => 'blue'],
    'visitor' => ['count' => 345, 'desc' => 'Total Pengunjung Web', 'color' => 'purple'], 
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
        // Melakukan loop pada semua data (Admin, Manager, Visitor)
        foreach ($user_counts_mini as $role => $data): 
        ?>
        <div class="col-lg-3 col-md-6 col-sm-6 col-xs-12">
            <div class="small-box role-card role-<?php echo $data['color']; ?>">
                <div class="inner">
                    <h3 class="role-title">
                        <?php 
                        // Menentukan Judul Card
                        if ($role === 'visitor') {
                            echo 'Pengunjung Web'; // Judul asli Anda
                        } else {
                            echo ucfirst($role);
                        }
                        ?> 
                        <span class="role-icon pull-right">
                            <?php 
                            // Menentukan Ikon Card
                            if ($role === 'visitor') {
                                echo '<i class="fa-solid fa-users"></i>'; 
                            } else {
                                echo '<i class="fa-solid fa-shield-alt"></i>';
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
                        
                        <table class="table table-striped table-bordered user-list-table dataTable">
                            <thead>
                                <tr>
                                    <th>Traffic Source</th>
                                    <th>Negara</th>
                                    <th>Kota</th>
                                    <th>Sesi</th>
                                    <th>Persentase</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><span class="label bg-red custom-label">Google Search</span></td>
                                    <td>Indonesia <span class="flag-icon flag-icon-id"></span></td>
                                    <td>Jakarta</td>
                                    <td>1.540</td>
                                    <td>55%</td>
                                </tr>
                                <tr>
                                    <td><span class="label bg-yellow custom-label">Instagram</span></td>
                                    <td>Malaysia <span class="flag-icon flag-icon-my"></span></td>
                                    <td>Kuala Lumpur</td>
                                    <td>500</td>
                                    <td>18%</td>
                                </tr>
                                <tr>
                                    <td><span class="label bg-blue custom-label">Direct / None</span></td>
                                    <td>Thailand <span class="flag-icon flag-icon-th"></span></td>
                                    <td>Bangkok</td>
                                    <td>320</td>
                                    <td>11%</td>
                                </tr>
                                <tr>
                                    <td><span class="label bg-green custom-label">Facebook</span></td>
                                    <td>Indonesia <span class="flag-icon flag-icon-id"></span></td>
                                    <td>Surabaya</td>
                                    <td>280</td>
                                    <td>10%</td>
                                </tr>
                            </tbody>
                        </table>
                        
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
            });
        });
    </script>
    
</section>
<?php
    include('includes/footer.php');
?>