<?php

?>

<h1>Add Admin Web</h1>
<hr>

<div id="response" class="alert alert-success" style="display:none;">
	<a href="#" class="close" data-dismiss="alert">&times;</a>
	<div class="message"></div>
</div>
						
<div class="row">
	<div class="col-xs-12">
		<div class="panel panel-default">
			<div class="panel-heading">
				<h4>User Website Information</h4>
			</div>
			<div class="panel-body form-group form-group-sm">
				<form method="post" id="add_user">
					<input type="hidden" name="action" value="add_user">
					<input type="hidden" name="platform" value="website">
					<div class="row">
						<!-- Nama -->
						<div class="col-xs-4">
							<label for="nama">Nama Lengkap</label>
							<input type="text" class="form-control margin-bottom required" name="nama" id="nama" placeholder="Masukkan Nama lengkap">
						</div>
						<!-- Email -->
						<div class="col-xs-4">
							<label for="email">Email</label>
							<input type="email" class="form-control margin-bottom required" name="email" id="email" placeholder="Masukkan Email" >
						</div>
						<!-- Username -->
						<div class="col-xs-4">
							<label for="username">Username</label>
							<input type="text" class="form-control margin-bottom required" name="username" id="username" placeholder="Masukkan Username">
						</div>
					</div>

					<div class="row">
						<!-- Level dropdown -->
						<div class="col-xs-4">
							<label for="level">Level</label>
							<select class="form-control margin-bottom required" name="level" id="level">
								<option value="">-- Pilih Level --</option>
								<option value="Admin">Admin</option>
								<option value="Developer">Developer</option>
							</select>
						</div>
						<!-- No HP -->
						<div class="col-xs-4">
							<label for="noHp">No. HP</label>
							<input type="number" class="form-control margin-bottom required" name="noHp" id="noHp" placeholder="Masukkan No.HP">
						</div>
						<!-- Password -->
						<div class="col-xs-6">
							<label for="password">Password</label>
							<input type="password" class="form-control margin-bottom" name="password" id="password" placeholder="Masukkan Password Baru">
						</div>
					</div>

					<div class="row">
						<div class="col-xs-12 margin-top btn-group">
							<input type="button" id="action_add_user" class="btn btn-success float-right" value="Add user" data-loading-text="Adding...">
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