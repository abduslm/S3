<?php
include('header-login.php');
include('includes/functions.php');

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
                            👁️
                        </button>
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
                    this.textContent = '👁️';
                    this.setAttribute('aria-label', 'Show password');
                }
            });
        </script>

    </body>
</html>
