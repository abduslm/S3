$(document).ready(function() {

	// Load dataTables
	$("#data-table").dataTable();

	// add user
	$(document).on('click', '#action_add_user', function(e) {
		e.preventDefault();
	    actionAddUser();
	});

	// update user
	$(document).on('click', '#action_update_user', function(e) {
		e.preventDefault();
	    actionUpdateUser();
	});

	// delete user
$(document).on('click', ".delete-user", function(e) {
    e.preventDefault();

    var userId = 'action=delete_user&delete='+ $(this).attr('data-user-id') + '&data-platform='+ $(this).attr('data-platform');
    var user = $(this);

    $('#k_delete_user').modal({ backdrop: 'static', keyboard: false }).one('click', '#delete', function() {
        deleteUser(userId);
        $(user).closest('tr').remove();
    });
});

	// login form
	$(document).bind('keypress', function(e) {
		e.preventDefault;
        if(e.keyCode==13){
            $('#btn-login').trigger('click');
        }
    });

	$(document).on('click','#btn-login', function(e){
		e.preventDefault;
		actionLogin();
	});



	function actionAddUser() {

		var errorCounter = validateForm();
		if (errorCounter > 0) {
		    $("#response").removeClass("alert-success").addClass("alert-warning").fadeIn();
		    $("#response .message").html("<strong>Error</strong>: Tolong isi semua field yang diperlukan!");
		    $("html, body").animate({ scrollTop: $('#response').offset().top }, 1000);
		} else {
			$(".required").parent().removeClass("has-error");
			var $btn = $("#action_add_user").button("loading");
			$.ajax({

				url: 'includes/response.php',
				type: 'POST',
				data: $("#add_user").serialize(),
				dataType: 'json',
				success: function(data){
					console.log(data);
					$("#response .message").html("<strong>" + data.status + "</strong>: " + data.message);
					
					if (data.status === 'Success') {
						$("#response").removeClass("alert-warning").addClass("alert-success").fadeIn();
						// Redirect ke list user setelah 2 detik
						setTimeout(function() {
							if(data.platform === "website") {
								$('[data-page="user-list-web"]').click();
							} else {
								$('[data-page="user-list-mobile"]').click();
							}
						}, 1000);
					} else {
						$("#response").removeClass("alert-success").addClass("alert-warning").fadeIn();
					}
					
					$("html, body").animate({ scrollTop: $('#response').offset().top }, 1000);
					$btn.button("reset");
				},
				error: function(data){
					console.log(data);
					$("#response .message").html("<strong>" + data.status + "</strong>: " + data.message);
					$("#response").removeClass("alert-success").addClass("alert-warning").fadeIn();
					$("html, body").animate({ scrollTop: $('#response').offset().top }, 1000);
					$btn.button("reset");
				}

			});
		}

	}

	function actionUpdateUser() {

		var errorCounter = validateForm();

		if (errorCounter > 0) {
		    $("#response").removeClass("alert-success").addClass("alert-warning").fadeIn();
		    $("#response .message").html("<strong>Error</strong>: Tolong isi semua field yang diperlukan!");
		    $("html, body").animate({ scrollTop: $('#response').offset().top }, 1000);
		} else {

			$(".required").parent().removeClass("has-error");

			var $btn = $("#action_update_user").button("loading");

			$.ajax({

				url: 'includes/response.php',
				type: 'POST',
				data: $("#update_user").serialize(),
				dataType: 'json',
				success: function(data){
					console.log(data);
					$("#response .message").html("<strong>" + data.status + "</strong>: " + data.message);
					
					if (data.status === 'Success') {
						$("#response").removeClass("alert-warning").addClass("alert-success").fadeIn();
						// Redirect ke list user setelah 2 detik
						setTimeout(function() {
							if(data.platform === "website") {
								$('[data-page="user-list-web"]').click();
							} else {
								$('[data-page="user-list-mobile"]').click();
							}
						}, 1000);
					} else {
						$("#response").removeClass("alert-success").addClass("alert-warning").fadeIn();
					}
					
					$("html, body").animate({ scrollTop: $('#response').offset().top }, 1000);
					$btn.button("reset");
				},
				error: function(xhr, status, error){
					console.log(xhr);
					$("#response .message").html("<strong>Error</strong>: Terjadi kesalahan sistem. Silakan coba lagi.");
					$("#response").removeClass("alert-success").addClass("alert-warning").fadeIn();
					$("html, body").animate({ scrollTop: $('#response').offset().top }, 1000);
					$btn.button("reset");
				}

			});
		}

	}

	function deleteUser(userId) {
    jQuery.ajax({
        url: 'includes/response.php',
        type: 'POST',
        data: userId,
        dataType: 'json',
        success: function(data){
            $("#response .message").html("<strong>" + data.status + "</strong>: " + data.message);
            $("#response").removeClass("alert-warning").addClass("alert-success").fadeIn();
            $("html, body").animate({ scrollTop: $('#response').offset().top }, 1000);
        },
        error: function(data){
            $("#response .message").html("<strong>" + data.status + "</strong>: " + data.message);
            $("#response").removeClass("alert-success").addClass("alert-warning").fadeIn();
            $("html, body").animate({ scrollTop: $('#response').offset().top }, 1000);
        } 
    });
}
   	// login function
	function actionLogin() {
	    var errorCounter = validateForm();

	    if (errorCounter > 0) {
	        $("#response").removeClass("alert-success").addClass("alert-warning").fadeIn();
	        $("#response .message").html("<strong>Error</strong>: Missing something are we? check and try again!");
	        $("html, body").animate({ scrollTop: $('#response').offset().top }, 1000);
			$btn.button("reset");
	    } else {
	        var $btn = $("#btn-login").button("loading");

	        jQuery.ajax({
	            url: 'includes/response.php',
	            type: "POST",
	            data: $("#login_form").serialize(),
	            dataType: 'json',
	            success: function(data){
	                $("#response .message").html("<strong>" + data.status + "</strong>: " + data.message);
	                $("#response").removeClass("alert-warning").addClass("alert-success").fadeIn();
	                $("html, body").animate({ scrollTop: $('#response').offset().top }, 1000);
	                $btn.button("reset");
					
					if(data.level === "Admin"){
						window.location.href = "header-adm.php";
					} else if(data.level === "Developer"){
						window.location.href = "header-dev.php";
					}else{
						window.location.href = "index.php";
					}
					
	            },
	            error: function(xhr, status, error){
					//$btn.button("reset");
	                $("#response .message").html("<strong>Error</strong>: Terjadi kesalahan sistem. Silakan coba lagi.");
	                $("#response").removeClass("alert-success").addClass("alert-warning").fadeIn();
	                $("html, body").animate({ scrollTop: $('#response').offset().top }, 1000);
	                $btn.button("reset");
	            }
	        });
	    }
	}


function validatePhoneNumber(phone) {
    // Remove non-digit characters
    var cleaned = phone.replace(/\D/g, '');
    
    // Check if consists only of digits
    if (!/^\d+$/.test(cleaned)) {
        return {
            valid: false,
            message: 'Nomor HP harus terdiri dari angka saja'
        };
    }
    
    // Check length
    if (cleaned.length < 10 || cleaned.length > 13) {
        return {
            valid: false,
            message: 'Nomor HP harus 10-13 digit angka'
        };
    }
    
    return {
        valid: true,
        cleaned: cleaned,
        message: 'Format nomor HP valid'
    };
}

function validateForm() {
    var errorCounter = 0;

    $(".required").each(function(i, obj) {
        if($(this).val() === ''){
            $(this).parent().addClass("has-error");
            errorCounter++;
        } else { 
            $(this).parent().removeClass("has-error"); 
            
            // Additional validation for phone number field
            if ($(this).attr('name') === 'noHp' || $(this).attr('name') === 'nohp') {
                var phoneValidation = validatePhoneNumber($(this).val());
                if (!phoneValidation.valid) {
                    $(this).parent().addClass("has-error");
                    errorCounter++;
                    // Show specific error message
                    $(this).next('.phone-error').remove();
                    $(this).after('<span class="help-block phone-error" style="color: #a94442;">' + phoneValidation.message + '</span>');
                } else {
                    $(this).next('.phone-error').remove();
                }
            }
        }
    });

    return errorCounter;
}

});