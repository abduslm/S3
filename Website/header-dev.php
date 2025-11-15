<?php
	//check login
	include("includes/session.php");
  
include('includes/functions.php');
?>


<!DOCTYPE html>

<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>Kedai Temeji</title>
  <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
 
  <!-- Font Awesome -->
  <link rel="stylesheet" href="css/fontawesome.min.css">
  <link rel="stylesheet" href="css/solid.min.css">

  
  
  
  <!-- Ionicons -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css">
  <!-- Theme style -->
  <link rel="stylesheet" href="css/AdminLTE.css">
 
  <link rel="stylesheet" href="css/skin1.css">
  
  	<!-- JS -->
	<script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
	<script src="js/moment.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="//cdn.datatables.net/1.10.7/js/jquery.dataTables.js"></script>
	<script src="//cdn.datatables.net/plug-ins/1.10.7/integration/bootstrap/3/dataTables.bootstrap.js"></script>
	<script src="js/bootstrap.datetime.js"></script>
	<script src="js/bootstrap.password.js"></script>
	<script src="js/scripts.js"></script>
	
	<!-- AdminLTE App -->
	<script src="js/app.min.js"></script>

	<!-- CSS -->
	<link rel="stylesheet" href="css/bootstrap.min.css">
	<link rel="stylesheet" href="css/bootstrap.datetimepicker.css">
	<link rel="stylesheet" href="//cdn.datatables.net/1.10.7/css/jquery.dataTables.css">
	<link rel="stylesheet" href="//cdn.datatables.net/plug-ins/1.10.7/integration/bootstrap/3/dataTables.bootstrap.css">
	<link rel="stylesheet" href="css/styles.css">

</head>

<body class="hold-transition skin-green sidebar-mini">
<div class="wrapper">

  <!-- Main Header -->
  <header class="main-header">

    <!--Logo -->
    <a href="" class="logo header-pad">
      <!--mini logo for sidebar mini 50x50 pixels -->
      <span class="logo-mini"><b>K</b>T</span>
      <!--logo for regular state and mobile devices -->
      <span style="text-decoration:none;" class="logo-lg"><b>Kedai</b> Temeji</span>
    </a>

    <!-- Header Navbar -->
    <nav class="navbar navbar-static-top" role="navigation">
      <!-- Sidebar toggle button-->
      <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
        <i class="fa-solid fa-bars"></i>
        <span class="sr-only">Toggle navigation</span>
      </a>
      <!-- Navbar Right Menu -->
      <div class="navbar-custom-menu">
        <ul class="nav navbar-nav">
        
          <!-- User Account Menu -->
          <li class="dropdown user user-menu">
            <!-- Menu Toggle Button -->
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
              <!-- The user image in the navbar-->
              <img src="images/logo.png" class="user-image" alt="User Image">
              <!-- hidden-xs hides the username on small devices so only the image appears. -->
              <span class="hidden-xs"><?php echo $_SESSION['login_username'];?></span>
            </a>
            <ul class="dropdown-menu">
            <!-- Drop down list-->
              <li><a href="includes/logout.php" class="btn btn-default btn-flat">Log out</a></li>
            </ul>
          </li>
        </ul>
      </div>
    </nav>
  </header>
  
  
  <!-- Left side column. contains the logo and sidebar -->
  <aside class="main-sidebar">

    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">


      <!-- Sidebar Menu -->
      <ul class="sidebar-menu">
        <li class="header">MENU</li>
        <!-- Menu 0.1 -->
        <li class="treeview">
          <a href="dashboard.php"><i class="fa-solid fa-tachometer"></i> <span>Dashboard</span>
            
          </a>
          
        
        </li>
        <!--  -->
        <li class="treeview">
          <a href="#"><i class="fa-solid fa-users"></i><span>Admin Mobile</span>
            <span class="pull-right-container">
              <i class="fa-solid fa-angle-left pull-right"></i>
            </span>
          </a>
          <ul class="treeview-menu">
            <li><a href="user-add.php"><i class="fa-solid fa-plus"></i>Add User</a></li>
            <li><a href="user-list.php"><i class="fa-solid fa-cog"></i>Manage Users</a></li>
          </ul>
        </li>

        <!-- Admin Web -->
        <li class="treeview">
          <a href="#"><i class="fa-solid fa-users-gear"></i></i><span>Admin Web</span>
            <span class="pull-right-container">
              <i class="fa-solid fa-angle-left pull-right"></i>
            </span>
          </a>
          <ul class="treeview-menu">
            <li><a href="user-add.php"><i class="fa-solid fa-plus"></i>Add User</a></li>
            <li><a href="user-list.php"><i class="fa-solid fa-cog"></i>Manage Users</a></li>
          </ul>
        </li>

        <!-- Riwayat Aktivitas -->
        <li class="treeview">
          <a href="dashboard.php"><i class="fa-solid fa-history"></i> <span>Riwayat Aktivitas</span>
            
          </a>
        </li>
        
      </ul>
      <!-- /.sidebar-menu -->
    </section>
    <!-- /.sidebar -->
  </aside>

  <!-- Content Wrapper. Contains page content -->
  <div class="content-wrapper">
  

    <!-- Main content -->
    <section class="content">
    <?php  
    include("user-list.php");
    ?>


