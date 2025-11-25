<?php

include('functions.php');
include_once('config.php');


ini_set('display_errors', 1);


if ($mysqli->connect_error) {
    die('Error : ('. $mysqli->connect_errno .') '. $mysqli->connect_error);
}

$action = isset($_POST['action']) ? $_POST['action'] : "";


// Login
if($action == 'login') {
    if ($mysqli->connect_error) {
        die('Error : ('. $mysqli->connect_errno .') '. $mysqli->connect_error);
    }

    session_start();
    
    extract($_POST);

    $username = mysqli_real_escape_string($mysqli,$_POST['username']);
    $pass_encrypt = mysqli_real_escape_string($mysqli,$_POST['password']);

    $query = "SELECT `id_userWeb`, `username`, `password`, `level` FROM `user_web` WHERE username='$username' AND `password` = '$pass_encrypt'";

    $results = mysqli_query($mysqli,$query) or die(mysqli_error($mysqli));
    $count = mysqli_num_rows($results);

    if($count > 0) {
        $row = $results->fetch_assoc();

		$_SESSION['login_id'] = $row['id_userWeb'];
        $_SESSION['login_username'] = $row['username'];
        $_SESSION['user_level'] = $row['level'];

        if (isset($_POST['remember'])) {    
            session_set_cookie_params('604800');
            session_regenerate_id(true);
        }  
        
        echo json_encode(array(
            'status' => 'Success',
            'message'=> 'Login Berhasil!',
            'level' => $row['level']
        ));
        
    } else {
        echo json_encode(array(
            'status' => 'Error',
            'message' => 'Login Gagal! Username atau password salah.'
        ));
    }
}


// Adding new user
if($action == 'add_user') {
	if ($mysqli->connect_error) {
	    die('Error : ('. $mysqli->connect_errno .') '. $mysqli->connect_error);
	}

	$user_nama = $_POST['nama'];
	$user_email = $_POST['email'];
	$user_nohp = $_POST['noHp'];
	$user_username = $_POST['username'];
	$user_password = $_POST['password'];
	$user_level = $_POST['level'];

	if(usernameCheck($user_username, $_POST['platform']) > 0) {
		echo json_encode(array(
			'status' => 'Error',
			'message' => 'Username sudah digunakan, silahkan gunakan username lain.'
		));
		exit();
	}
	$phone_valid = validatePhoneNumber($user_nohp);
    if (!$phone_valid['valid']) {
        echo json_encode(array(
            'status' => 'Error',
            'message' => $phone_valid['message']
        ));
        exit;
    }
    $user_nohp = $phone_valid['cleaned'];



	if($_POST['platform']=='website'){
		$query  = "INSERT INTO user_web (`Nama`, `Email`, `NoHp`, `username`, `password`, `level`, `Status`) VALUES (?,?,?,?,?,?,'OFFLINE');";
	}else{
		$query  = "INSERT INTO user_mobile (`Nama`, `Email`, `NoHp`, `username`, `password`, `level`, `Status`) VALUES (?,?,?,?,?,?,'OFFLINE');";
	}
	
	header('Content-Type: application/json');

	$stmt = $mysqli->prepare($query);
	if($stmt === false) {
		trigger_error('Wrong SQL: ' . $query . ' Error: ' . $mysqli->error, E_USER_ERROR);
	}
	$user_password = $user_password; //md5

	$stmt->bind_param('ssssss', $user_nama, $user_email, $user_nohp, $user_username,$user_password,$user_level);

	if($stmt->execute()){
		echo json_encode(array(
			'status' => 'Success',
			'message'=> 'User berhasil ditambahkan!',
			'platform' => $_POST['platform']
		));

	} else {
		echo json_encode(array(
			'status' => 'Error',
			'message' => 'Ada kesalahan, mohon ulangi lagi.<pre>'.$mysqli->error.'</pre><pre>'.$query.'</pre>'
		));
	}
	$mysqli->close();
}

// Update user
if($action == 'update_user') {

	if ($mysqli->connect_error) {
	    die('Error : ('. $mysqli->connect_errno .') '. $mysqli->connect_error);
	}

	$getID = $_POST['id'];
	$user_nama = $_POST['nama'];
	$user_email = $_POST['email'];
	$user_nohp = $_POST['noHp'];
	$user_username = $_POST['username'];
	$user_password = $_POST['password'];
	$user_level = $_POST['level'];
	$user_lama= isset($_POST['usernameL']) ? $_POST['usernameL'] : $user_username;

	if($user_username != $user_lama) {
		if(usernameCheck($user_username, $_POST['platform']) > 0) {
			echo json_encode(array(
				'status' => 'Error',
				'message' => 'Username sudah digunakan, silahkan gunakan username lain.'
			));
			exit();
		}
	}

	$phone_valid = validatePhoneNumber($user_nohp);
    if (!$phone_valid['valid']) {
        echo json_encode(array(
            'status' => 'Error',
            'message' => $phone_valid['message']
        ));
        exit;
    }
    $user_nohp = $phone_valid['cleaned'];

	if($_POST['platform']=='website'){
		if($user_password == ''){
			$query = "UPDATE user_web SET Nama=?, Email=?, NoHp=?, username=?, level=? WHERE id_userWeb = ?";
		}else{
			$query = "UPDATE user_web SET Nama=?, Email=?, NoHp=?, username=?, password=?, level=? WHERE id_userWeb = ?";
		}
	}elseif($_POST['platform']=='mobile'){
		if($user_password == ''){
			$query = "UPDATE user_mobile SET Nama=?, Email=?, NoHp=?, username=?, level=? WHERE id_userMobile = ?";
		}else{
			$query = "UPDATE user_mobile SET Nama=?, Email=?, NoHp=?, username=?, password=?, level=? WHERE id_userMobile = ?";
		}
	}

	$stmt = $mysqli->prepare($query);
	if($stmt === false) {
		trigger_error('Wrong SQL: ' . $query . ' Error: ' . $mysqli->error, E_USER_ERROR);
	}

	if($user_password == ''){
		$stmt->bind_param(
			'ssssss',
			$user_nama,$user_email,$user_nohp,$user_username,$user_level,$getID
		);
	} else {
		$user_password = $user_password; //md5
		$stmt->bind_param(
			'sssssss',
			$user_nama,$user_email,$user_nohp,$user_username,$user_password,$user_level,$getID
		);
	}

	if($stmt->execute()){
		echo json_encode(array(
			'status' => 'Success',
			'message'=> 'User website telah berhasil di update!',
			'platform' => $_POST['platform']
		));

	} else {
	    echo json_encode(array(
	    	'status' => 'Error',
	    	'message' => 'Terdapat kesalahan, mohon ulangi lagi.<pre>'.$mysqli->error.'</pre><pre>'.$query.'</pre>'
	    ));
	}
	$mysqli->close();
}

// Delete User web
if($action == 'delete_user_web') {

	if ($mysqli->connect_error) {
	    die('Error : ('. $mysqli->connect_errno .') '. $mysqli->connect_error);
	}
	$id = $_POST["delete"];

	$query = "DELETE FROM `user_web` WHERE id = ?";

	$stmt = $mysqli->prepare($query);
	if($stmt === false) {
		trigger_error('Wrong SQL: ' . $query . ' Error: ' . $mysqli->error, E_USER_ERROR);
	}

	$stmt->bind_param('s',$id);

	if($stmt->execute()){

		echo json_encode(array(
			'status' => 'Success',
			'message'=> 'User berhasil di hapus!'
		));

	} else {
	    echo json_encode(array(
	    	'status' => 'Error',
	      	'message' => 'Terdapat kesalahan, mohon ulangi lagi.<pre>'.$mysqli->error.'</pre><pre>'.$query.'</pre>'
	    ));
	}
	$mysqli->close();

}

// Delete User
if($action == 'delete_user') {

    if ($mysqli->connect_error) {
        die('Error : ('. $mysqli->connect_errno .') '. $mysqli->connect_error);
    }

    $id = intval($_POST["delete"]);
    $pp = $_POST['data-platform'];

    if($pp == 'website'){
        $query = "DELETE FROM `user_web` WHERE id_userWeb = ?";
    } elseif($pp == 'mobile'){
        $query = "DELETE FROM `user_mobile` WHERE id_userMobile = ?";
    } else {
        $query = '';
    }

    $stmt = $mysqli->prepare($query);
    if($stmt === false) {
        trigger_error('Wrong SQL: ' . $query . ' Error: ' . $mysqli->error, E_USER_ERROR);
    }

    $stmt->bind_param('i', $id);

    if($stmt->execute()){
        echo json_encode(array(
            'status' => 'Success',
            'message'=> 'User berhasil dihapus!'
        ));
    } else {
        echo json_encode(array(
            'status' => 'Error',
            'message' => 'Terdapat kesalahan, mohon ulangi lagi.<pre>'.$mysqli->error.'</pre><pre>'.$query.'</pre>'
        ));
    }
    $mysqli->close();
}
?>
