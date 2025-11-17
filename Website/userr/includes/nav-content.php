<?php
// Session dan security check
include("session.php");
include('functions.php');

if(isset($_POST['page'])) {
    $page = $_POST['page'];
    $idd = isset($_POST['idd']) ? $_POST['idd'] : '';
    
    // Validasi dan sanitize input
    $allowed_pages = array(
        'dashboard',
        'user-add-mobile', 
        'user-list-mobile',
        'user-edit-mobile',
        'user-add-web',
        'user-list-web',
        'user-edit-web',
        'activity-history'
    );
    
    if(in_array($page, $allowed_pages)) {
        // Include file yang sesuai
        switch($page) {
            case 'dashboard':
                include('../dashboard.php');
                break;
            case 'user-add-mobile':
                include('../user-add-mobile.php');
                break;
            case 'user-list-mobile':
                include('../user-list-mobile.php');
                break;
            case 'user-add-web':
                include('../user-add-web.php');
                break;
            case 'user-edit-mobile':
                include('../user-edit-mobile.php'.$idd);
                break;
            case 'user-edit-web':
                include('../user-edit-web.php'.$idd.'');
                break;
            case 'user-list-web':
                include('../user-list-web.php');
                break;
            case 'activity-history':
                include('../dashboard.php');
                break;
            default:
                echo '<div class="alert alert-warning">Page not found</div>';
        }
    } else {
        echo '<div class="alert alert-danger">Invalid page request</div>';
    }
} else {
    echo '<div class="alert alert-warning">No page specified</div>';
}
?>