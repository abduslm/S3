<?php


include_once("config.php");

// get user list
function getUsers(string $plat) {

	$mysqli = new mysqli(DATABASE_HOST, DATABASE_USER, DATABASE_PASS, DATABASE_NAME);

	if ($mysqli->connect_error) {
	    die('Error : ('. $mysqli->connect_errno .') '. $mysqli->connect_error);
	}

	$query;
	if($plat='website'){
		$query = "SELECT `id_userWeb`,`Nama`, `Email`, `NoHp`, `username`, `level`, `Status` FROM `user_web` ";
	}elseif($plat='mobile'){
		$query = "SELECT `id_userMobile`, `Nama`, `Email`, `NoHp`, `username`, `level`, `Status` FROM `user_mobile` ";
	}
	// mysqli select query
	$results = $mysqli->query($query);

	if($results) {

		print '<table class="table table-striped table-hover table-bordered" id="data-table"><thead><tr>

				<th>Nama</th>
				<th>Username</th>
				<th>Email</th>
				<th>No.Hp</th>
				<th>Level</th>
				<th>Status</th>
				<th>Action</th>

			</tr></thead><tbody>';

		while($row = $results->fetch_assoc()) {

			if($plat=='website'){
				print '
			    <tr>
			    	<td>'.$row['Nama'].'</td>
					<td>'.$row["username"].'</td>
					<td>'.$row["Email"].'</td>
					<td>'.$row["NoHp"].'</td>
				    <td>'.$row["level"].'</td>
					<td>'.$row["Status"].'</td>
				    <td><a href="../user-edit-web.php?id='.$row["id_userWeb"].'" class="btn btn-primary btn-xs"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span></a> <a data-user-id="'.$row["id_userWeb"].'" class="btn btn-danger btn-xs delete-user"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a></td>
			    </tr>
		    ';
			}elseif($plat=='mobile'){
				print '
			    <tr>
			    	<td>'.$row['Nama'].'</td>
					<td>'.$row["username"].'</td>
					<td>'.$row["Email"].'</td>
					<td>'.$row["NoHp"].'</td>
				    <td>'.$row["level"].'</td>
					<td>'.$row["Status"].'</td>
				    <td><a href="../user-edit-mobile.php?id='.$row["id_userMobile"].'" class="btn btn-primary btn-xs"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span></a> <a data-user-id="'.$row["id_userMobile"].'" class="btn btn-danger btn-xs delete-user"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a></td>
			    </tr>
		    ';
			}
		    
		}

		print '</tr></tbody></table>';

	} else {

		echo "<p>There are no users to display.</p>";

	}

	$results->free();

	$mysqli->close();
}

?>

