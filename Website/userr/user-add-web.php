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
							<input type="text" class="form-control margin-bottom required" name="nama" placeholder="Masukkan Nama lengkap">
						</div>
						<!-- email -->
						<div class="col-xs-4">
							<input type="text" class="form-control margin-bottom required" name="email" placeholder="Masukkan Email">
						</div>
						<!-- Username -->
						<div class="col-xs-4">
							<input type="text" class="form-control margin-bottom required" name="username" placeholder="Masukkan Username">
						</div>
						
					</div>

					<div class="row">
						<!-- Level dropdown -->
						<div class="col-xs-4">
							<select class="form-control margin-bottom required" name="level">
								<option value="">-- Pilih Level --</option>
								<option value="Admin">Admin</option>
								<option value="Developer">Developer</option>
							</select>
						</div>
						<!-- nohp -->
						<div class="col-xs-4">
							<input type="text" class="form-control margin-bottom required" name="noHp" placeholder="Masukkan No.HP">
						</div>
						<!-- Password -->
						<div class="col-xs-4">
							<input type="text" class="form-control margin-bottom required" name="password" placeholder="Masukkan Password">
						</div>
					</div>

					<div class="row">
						<div class="col-xs-12 margin-top btn-group">
							<input type="submit" id="action_add_user" class="btn btn-success float-right" value="Add user" data-loading-text="Adding...">
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