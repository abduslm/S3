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


	$user_nama = $_POST['nama'];
	$user_email = $_POST['email'];
	$user_nohp = $_POST['noHp'];
	$user_username = $_POST['username'];
	$user_password = $_POST['password'];
	$user_level = $_POST['level'];
	$platformm;
	if($user_platform==$_POST['website']){
		$platformm='user_web';
	}elseif($user_platform==$_POST['mobile']){
		$platformm='user_mobile';
	}
	//insert query
	$query  = "INSERT INTO ? (`Nama`, `Email`, `NoHp`, `username`, `password`, `level`, `Status`) VALUES (?,?,?,?,?,?,'OFFLINE');";

	header('Content-Type: application/json');

	$stmt = $mysqli->prepare($query);
	if($stmt === false) {
		trigger_error('Wrong SQL: ' . $query . ' Error: ' . $mysqli->error, E_USER_ERROR);
	}
	$user_password = $user_password; //md5

	$stmt->bind_param('sssssss', $platformm, $user_nama, $user_email, $user_nohp, $user_username,$user_password,$user_level);

	if($stmt->execute()){
		echo json_encode(array(
			'status' => 'Success',
			'message'=> 'User Website berhasil ditambahkan!'
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
	$platformm;
	if($user_platform==$_POST['website']){
		$platformm='user_web';
	}elseif($user_platform==$_POST['mobile']){
		$platformm='user_mobile';
	}

	if($password == ''){
		// query 1
		$query = "UPDATE ? SET `Nama`=?,`Email`=?,`NoHp`=?,`username`=?,`level`=? WHERE id_userWeb = ?";
		// query 2
		$query = "UPDATE ? SET `Nama`=?,`Email`=?,`NoHp`=?,`username`=?,`password`=?,`level`=? WHERE id_userWeb = ?";
	}

	$stmt = $mysqli->prepare($query);
	if($stmt === false) {
		trigger_error('Wrong SQL: ' . $query . ' Error: ' . $mysqli->error, E_USER_ERROR);
	}

	if($password == ''){
		$stmt->bind_param(
			'sssssss',
			$platformm,$user_nama,$user_email,$user_nohp,$user_username,$user_level,$getID
		);
	} else {
		$password = md5($password);
		$stmt->bind_param(
			'ssssssss',
			$platformm,$user_nama,$user_email,$user_nohp,$user_username,$user_password,$user_level,$getID
		);
	}

	if($stmt->execute()){
		echo json_encode(array(
			'status' => 'Success',
			'message'=> 'User website telah berhasil di update!'
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


// Delete User mobile
if($action == 'delete_user_mobile') {

	if ($mysqli->connect_error) {
	    die('Error : ('. $mysqli->connect_errno .') '. $mysqli->connect_error);
	}
	$id = $_POST["delete"];

	$query = "DELETE FROM `user_mobile` WHERE id = ?";

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
?>
