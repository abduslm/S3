
$(document).ready(function() {

	// Load dataTables
	$("#data-table").dataTable();

	// password strength
	var options = {
        onLoad: function () {
            $('#messages').text('Start typing password');
        },
        onKeyUp: function (evt) {
            $(evt.target).pwstrength("outputErrorList");
        }
    };
    $('#password').pwstrength(options);

	// add user
	$("#action_add_user").click(function(e) {
		e.preventDefault();
	    actionAddUser();
	});

	// delete user
	$(document).on('click', ".delete-user", function(e) {
        e.preventDefault();

        var userId = 'action=delete_user&delete='+ $(this).attr('data-user-id'); //build a post data structure
        var user = $(this);

	    $('#delete_user').modal({ backdrop: 'static', keyboard: false }).one('click', '#delete', function() {
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

				url: 'response.php',
				type: 'POST',
				data: $("#add_user").serialize(),
				dataType: 'json',
				success: function(data){
					console.log(data);
					$("#response .message").html("<strong>" + data.status + "</strong>: " + data.message);
					$("#response").removeClass("alert-warning").addClass("alert-success").fadeIn();
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
				$btn.button("reset");
			},
			error: function(data){
				$("#response .message").html("<strong>" + data.status + "</strong>: " + data.message);
				$("#response").removeClass("alert-success").addClass("alert-warning").fadeIn();
				$("html, body").animate({ scrollTop: $('#response').offset().top }, 1000);
				$btn.button("reset");
			} 
    	});

   	}


   	function updateUser() {

   		var $btn = $("#action_update_user").button("loading");

        jQuery.ajax({

        	url: 'includes/response.php',
            type: 'POST', 
            data: $("#update_user").serialize(),
            dataType: 'json', 
            success: function(data){
				$("#response .message").html("<strong>" + data.status + "</strong>: " + data.message);
				$("#response").removeClass("alert-warning").addClass("alert-success").fadeIn();
				$("html, body").animate({ scrollTop: $('#response').offset().top }, 1000);
				$btn.button("reset");
			},
			error: function(data){
				$("#response .message").html("<strong>" + data.status + "</strong>: " + data.message);
				$("#response").removeClass("alert-success").addClass("alert-warning").fadeIn();
				$("html, body").animate({ scrollTop: $('#response').offset().top }, 1000);
				$btn.button("reset");
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

                window.location.href = "heade.php";
            },
            error: function(xhr, status, error){
                $("#response .message").html("<strong>Error</strong>: Terjadi kesalahan sistem. Silakan coba lagi.");
                $("#response").removeClass("alert-success").addClass("alert-warning").fadeIn();
                $("html, body").animate({ scrollTop: $('#response').offset().top }, 1000);
                $btn.button("reset");
            }
        });
    }
}

function validateForm() {
    var errorCounter = 0;

    $(".required").each(function(i, obj) {
        if($(this).val() === ''){
            $(this).parent().addClass("has-error");
            errorCounter++;
        } else { 
            $(this).parent().removeClass("has-error"); 
        }
    });

    return errorCounter;
}


});