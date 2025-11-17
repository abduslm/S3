<?php

include('heade.php');
$getID = $_GET['id'];

// output any connection error
if ($mysqli->connect_error) {
	die('Error : ('.$mysqli->connect_errno .') '. $mysqli->connect_error);
}

// the query
$query = "SELECT `id_userWeb`, `Nama`, `Email`, `NoHp`, `username`, `password`, `level` FROM `user_web` WHERE id_userWeb= '" . $mysqli->real_escape_string($getID) . "'";

$result = mysqli_query($mysqli, $query);

// mysqli select queryjv
if($result) {
	while ($row = mysqli_fetch_assoc($result)) {
		$idUser = $row['id_userWeb']; // id
		$nama = $row['Nama']; // nama
		$email = $row['Email']; // email
		$nohp = $row['NoHp']; // nohp
		$username = $row['username']; // username
		$password = $row['password']; // email address
		$level = $row['level']; // phone number
	}
}

/* close connection */
$mysqli->close();

?>

<h1>Edit User</h1>
<hr>

<div id="response" class="alert alert-success" style="display:none;">
	<a href="#" class="close" data-dismiss="alert">&times;</a>
	<div class="message"></div>
</div>
						
<div class="row">
	<div class="col-xs-12">
		<div class="panel panel-default">
			<div class="panel-heading">
				<h4>Editing User (<?php echo $getID; ?>)</h4>
			</div>
			<div class="panel-body form-group form-group-sm">
				<form method="post" id="update_user">
					<input type="hidden" name="action" value="update_user">
					<input type="hidden" name="platform" value="website">
					<input type="hidden" name="id" value="<?php echo $getID; ?>">


					<div class="row">
						<!-- Nama -->
						<div class="col-xs-4">
							<input type="text" class="form-control margin-bottom required" name="nama" placeholder="Masukkan Nama lengkap" value="<?php echo $nama; ?>">
						</div>
						<!-- email -->
						<div class="col-xs-4">
							<input type="text" class="form-control margin-bottom required" name="email" placeholder="Masukkan Email" value="<?php echo $email; ?>">
						</div>
						<!-- Username -->
						<div class="col-xs-4">
							<input type="text" class="form-control margin-bottom required" name="username" placeholder="Masukkan Username" value="<?php echo $username; ?>">
						</div>
						
					</div>

					<div class="row">
						<!-- Level dropdown -->
						<div class="col-xs-4">
							<select class="form-control margin-bottom required" name="level" value="<?php echo $level; ?>">
								<option value="">-- Pilih Level --</option>
								<option value="Admin">Admin</option>
								<option value="Developer">Developer</option>
							</select>
						</div>
						<!-- nohp -->
						<div class="col-xs-4">
							<input type="text" class="form-control margin-bottom required" name="noHp" placeholder="Masukkan No.HP" value="<?php echo $nohp; ?>">
						</div>
						<!-- Password -->
						<div class="col-xs-4">
							<input type="text" class="form-control margin-bottom required" name="password" placeholder="Masukkan Password" value="<?php echo $password; ?>">
						</div>
					</div>

					<div class="row">
						<div class="col-xs-12 margin-top btn-group">
							<input type="submit" id="action_update_user" class="btn btn-success float-right" value="Edit user" data-loading-text="Editing...">
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
<div>

<?php
	include('includes/footer.php');
?>