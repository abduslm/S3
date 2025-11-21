<?php

include_once("config.php");

// get user list
function getUsers(string $plat) {

	$mysqli = new mysqli(DATABASE_HOST, DATABASE_USER, DATABASE_PASS, DATABASE_NAME);

	if ($mysqli->connect_error) {
	    die('Error : ('. $mysqli->connect_errno .') '. $mysqli->connect_error);
	}

	$query;
	if($plat=='website'){
		$query = "SELECT `id_userWeb`,`Nama`, `Email`, `NoHp`, `username`, `level`, `Status` FROM `user_web` ";
	}elseif($plat=='mobile'){
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
				    <td><a href="#" class="ajax-menu edit-user" data-page="user-edit-web" data-user-id="'.$row["id_userWeb"].'" data-platform="website"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span></a> <a data-user-id="'.$row["id_userWeb"].'" data-platform="website" class="btn btn-danger btn-xs delete-user"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a></td>
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
				    <td><a href="#" class="ajax-menu edit-user" data-page="user-edit-mobile" data-user-id="'.$row["id_userMobile"].'" data-platform="mobile"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span></a> <a data-user-id="'.$row["id_userMobile"].'" data-platform="mobile" class="btn btn-danger btn-xs delete-user"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a></td>
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


function usernameCheck($username, $platform) {
	$mysqli = new mysqli(DATABASE_HOST, DATABASE_USER, DATABASE_PASS, DATABASE_NAME);

	if ($mysqli->connect_error) {
	    die('Error : ('. $mysqli->connect_errno .') '. $mysqli->connect_error);
	}

	$query;
	if($platform=='website'){
		$query = "SELECT `username` FROM `user_web` WHERE `username` = '$username' ";
	}elseif($platform=='mobile'){
		$query = "SELECT `username` FROM `user_mobile` WHERE `username` = '$username' ";
	}
	// mysqli select query
	$results = $mysqli->query($query);
	$count = mysqli_num_rows($results);

	$results->free();

	$mysqli->close();

	return $count;
}


function validatePhoneNumber($phone) {
    // Remove any non-digit characters
    $cleaned_phone = preg_replace('/[^0-9]/', '', $phone);
    
    // Check if the cleaned number consists only of digits
    if (!ctype_digit($cleaned_phone)) {
        return array(
            'valid' => false,
            'message' => 'Nomor HP harus terdiri dari angka saja'
        );
    }
    
    // Check length (10-13 digits)
    $length = strlen($cleaned_phone);
    if ($length < 10 || $length > 13) {
        return array(
            'valid' => false,
            'message' => 'Nomor HP harus terdiri dari 10-13 digit angka'
        );
    }
    
    // Check if it starts with valid prefix (optional)
    $prefix = substr($cleaned_phone, 0, 2);
    $valid_prefixes = ['08', '62']; // Indonesia common prefixes
    
    if (!in_array($prefix, $valid_prefixes) && $prefix != '08' && substr($prefix, 0, 1) != '8') {
        return array(
            'valid' => false,
            'message' => 'Nomor HP harus diawali dengan 08 atau 62'
        );
    }
    
    return array(
        'valid' => true,
        'cleaned' => $cleaned_phone,
        'message' => 'Format nomor HP valid'
    );
}

?>