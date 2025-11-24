<?php
include("includes/session.php");
include('includes/functions.php');

// Ambil ID dari parameter
$getID = isset($_POST['id']) ? intval($_POST['id']) : (isset($_GET['id']) ? intval($_GET['id']) : 0);

// output any connection error
if ($mysqli->connect_error) {
    die('Error : ('. $mysqli->connect_errno .') '. $mysqli->connect_error);
}

// the query
$query = "SELECT `id_userWeb`, `Nama`, `Email`, `NoHp`, `username`, `password`, `level`, `Status` FROM `user_web` WHERE id_userWeb= '" . $mysqli->real_escape_string($getID) . "'";

$result = mysqli_query($mysqli, $query);

// mysqli select query
if($result) {
    while ($row = mysqli_fetch_assoc($result)) {
        $idUser = $row['id_userWeb']; // id
        $nama = $row['Nama']; // nama
        $email = $row['Email']; // email
        $nohp = $row['NoHp']; // nohp
        $username = $row['username']; // username
        $password = $row['password']; // password
        $level = $row['level']; // level
        $status = $row['Status']; // status
    }
}

/* close connection */
$mysqli->close();
?>

<div class="row">
    <div class="col-xs-12">
        <div class="box box-primary">
            <div class="box-header with-border">
                <h3 class="box-title">Edit User Web</h3>
                <div class="box-tools">
                    <a href="#" class="btn btn-default btn-xs ajax-menu" data-page="user-list-web">
                        <i class="fa fa-arrow-left"></i> Back to User List
                    </a>
                </div>
            </div>
            
            <div class="box-body">
                <?php if ($getID > 0 && isset($nama)): ?>
                    <div id="response" class="alert alert-success" style="display:none;">
                        <a href="#" class="close" data-dismiss="alert">&times;</a>
                        <div class="message"></div>
                    </div>
                    
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h4>Editing User (<?php echo $getID; ?>)</h4>
                        </div>
                        <div class="panel-body form-group form-group-sm">
                            <form method="post" id="update_user">
                                <input type="hidden" name="action" value="update_user">
                                <input type="hidden" name="platform" value="website">
                                <input type="hidden" name="usernameL" value="<?php echo htmlspecialchars($username); ?>">
                                <input type="hidden" name="id" value="<?php echo $getID; ?>">

                                <div class="row">
                                    <!-- Nama -->
                                    <div class="col-xs-4">
                                        <label for="nama">Nama Lengkap</label>
                                        <input type="text" class="form-control margin-bottom required" name="nama" id="nama" placeholder="Masukkan Nama lengkap" value="<?php echo htmlspecialchars($nama); ?>">
                                    </div>
                                    <!-- Email -->
                                    <div class="col-xs-4">
                                        <label for="email">Email</label>
                                        <input type="email" class="form-control margin-bottom required" name="email" id="email" placeholder="Masukkan Email" value="<?php echo htmlspecialchars($email); ?>">
                                    </div>
                                    <!-- Username -->
                                    <div class="col-xs-4">
                                        <label for="username">Username</label>
                                        <input type="text" class="form-control margin-bottom required" name="username" id="username" placeholder="Masukkan Username" value="<?php echo htmlspecialchars($username); ?>">
                                    </div>
                                </div>

                                <div class="row">
                                    <!-- Level dropdown -->
                                    <div class="col-xs-4">
                                        <label for="level">Level</label>
                                        <select class="form-control margin-bottom required" name="level" id="level">
                                            <option value="">-- Pilih Level --</option>
                                            <option value="Admin" <?php echo ($level == 'Admin') ? 'selected' : ''; ?>>Admin</option>
                                            <option value="Developer" <?php echo ($level == 'Developer') ? 'selected' : ''; ?>>Developer</option>
                                        </select>
                                    </div>
                                    <!-- No HP -->
                                    <div class="col-xs-4">
                                        <label for="noHp">No. HP</label>
                                        <input type="number" class="form-control margin-bottom required" name="noHp" id="noHp" placeholder="Masukkan No.HP" value="<?php echo htmlspecialchars($nohp); ?>">
                                    </div>
									<!-- Password -->
									<div class="col-xs-6">
                                        <label for="password">Password (Kosongkan jika tidak ingin mengubah)</label>
                                        <input type="password" class="form-control margin-bottom" name="password" id="password" placeholder="Masukkan Password Baru">
                                    </div>
                                </div>

                                <div class="row">
                                    
                                    
                                    <div class="col-xs-6">
                                        <label>&nbsp;</label>
                                        <div style="margin-top: 25px;">
                                            <input type="submit" id="action_update_user" class="btn btn-success" value="Update User" data-loading-text="Updating...">
                                            <a href="#" class="btn btn-default ajax-menu" data-page="user-list-web">Cancel</a>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                <?php else: ?>
                    <div class="alert alert-danger">
                        <i class="fa fa-exclamation-triangle"></i> User tidak ditemukan!
                    </div>
                    <a href="#" class="btn btn-default ajax-menu" data-page="user-list-web">
                        <i class="fa fa-arrow-left"></i> Back to User List
                    </a>
                <?php endif; ?>
            </div>
        </div>
    </div>
</div>