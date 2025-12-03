<?php
include('header-login.php');
include('includes/functions.php');

session_start();
include_once('includes/config.php');

// Connect to database
$mysqli = new mysqli(DATABASE_HOST, DATABASE_USER, DATABASE_PASS, DATABASE_NAME, DATABASE_PORT);

// Jika user sudah login, redirect ke dashboard sesuai level
if(isset($_SESSION['login_username'])) {
    if(isset($_SESSION['user_level']) && $_SESSION['user_level'] == 'Admin') {
        header("Location: header-adm.php");
    } else if(isset($_SESSION['user_level']) && $_SESSION['user_level'] == 'Developer') {
        header("Location: header-dev.php");
    }
    exit();
}

// Cek cookie remember me hanya jika belum login
if(isset($_COOKIE['remember_token'])) {
    $token = $_COOKIE['remember_token'];
    $query = "SELECT id_userWeb, username, level FROM user_web WHERE remember_token = ? AND token_expiry > NOW()";
    $stmt = $mysqli->prepare($query);
    $stmt->bind_param("s", $token);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if($result->num_rows > 0) {
        $user = $result->fetch_assoc();

        $_SESSION['login_username'] = $user['username'];
        $_SESSION['login_id'] = $user['id_userWeb'];
        $_SESSION['user_level'] = $user['level'];
        
        // Redirect sesuai level
        if($user['user_level'] == 'admin') {
            header("Location: header-adm.php");
        } else {
            header("Location: header-dev.php");
        }
        exit();
    } else {
        // Token tidak valid, hapus cookie
        setcookie('remember_token', '', time() - 3600, '/');
    }
}

?>
    <body>
		<div id="response" class="alert alert-success" style="display:none;">
			<a href="#" class="close" data-dismiss="alert">&times;</a>
			<div class="message"></div>
		</div>
		<div class="login-SPV">
            <div class="login">
                <img class="image" src="images/image_1.png" alt="Background Coffee Beans" />
                <div class="rectangle"></div>
                <img class="img" src="images/Rectangle-15.png" alt="Overlay" />
                
                
                <img class="rectangle-2" src="images/Rectangle-12.png" alt="Login Card Background" />
                <img class="logo-kedai-temeji" src="images/logo-kedai-temeji-v6-1.png" alt="Logo" />
                
                <div class="text-wrapper-3">LOGIN</div>

                <form class="login-form" id="login_form" method="POST">
					<input type="hidden" name="action" value="login">
                    <label for="username" class="text-wrapper">Username</label>
                    <input type="text" id="username" name="username" class="input-field username-field" placeholder="Masukkan Username" required />
                    
                    <label for="password" class="text-wrapper-2">Password</label>
                    <div class="password-container">
                        <input type="password" id="password" name="password" class="input-field password-field" placeholder="Masukkan Password" required />
                        <button type="button" class="toggle-password" id="togglePassword" aria-label="Show password">
                            👁
                        </button>
                    </div>

                    <div class="checkbox rememberr">
                        <label color="blue">
                            <input name="remember" type="checkbox" value="Remember Me"> Remember Me
                        </label>
			    	</div>

                    <button type="button" id="btn-login" class="button-login">
                        <div class="rectangle-4"></div>
                        <div class="text-wrapper-4">SIGN IN</div>
                    </button>
                </form>                
            </div>
        </div>
        

        <script>
            const togglePassword = document.getElementById('togglePassword');
            const passwordInput = document.getElementById('password');

            togglePassword.addEventListener('click', function () {
                const type =
                    passwordInput.getAttribute('type') === 'password'
                        ? 'text'
                        : 'password';
                passwordInput.setAttribute('type', type);

                // Change icon
                if (type === 'text') {
                    this.textContent = '🔒';
                    this.setAttribute('aria-label', 'Hide password');
                } else {
                    this.textContent = '👁';
                    this.setAttribute('aria-label', 'Show password');
                }
            });
        </script>

    </body>
</html>