// AJAX Navigation for Kedai Temeji
$(document).ready(function() {
    // Fungsi untuk memuat konten via AJAX
    function loadContent(page) {
        // Show loading indicator
        $('#main-content').html(
            '<div class="text-center" style="padding: 50px;">' +
            '<i class="fa fa-spinner fa-spin fa-3x"></i>' +
            '<p>Loading...</p>' +
            '</div>'
        );

        $.ajax({
            url: 'includes/nav-content.php',
            type: 'POST',
            data: { page: page },
            success: function(data) {
                $('#main-content').html(data);
                
                // Re-initialize components if needed
                initializeComponents();
            },
            error: function(xhr, status, error) {
                $('#main-content').html(
                    '<div class="alert alert-danger">' +
                    '<i class="fa fa-exclamation-triangle"></i> ' +
                    'Error loading content: ' + error +
                    '</div>'
                );
            }
        });
    }

    // Fungsi untuk inisialisasi komponen setelah load content
    function initializeComponents() {
        // Initialize DataTables jika ada
        if ($.fn.DataTable) {
            $('.data-table').DataTable();
        }
        
        // Initialize datetime picker jika ada
        if ($.fn.datetimepicker) {
            $('.datetimepicker').datetimepicker();
        }
        
        // Initialize password strength jika ada
        if ($.fn.password) {
            $('.password').password();
        }
    }

    // Event handler untuk menu
    $('.sidebar-menu').on('click', '.ajax-menu', function(e) {
        e.preventDefault();
        var page = $(this).data('page');
        
        // Update active menu
        $('.sidebar-menu li').removeClass('active');
        $(this).closest('li').addClass('active');
        
        // Untuk treeview menu, set parent juga aktif
        var treeviewParent = $(this).closest('.treeview-menu').closest('li');
        if (treeviewParent.length) {
            treeviewParent.addClass('active');
        }
        
        // Update URL tanpa reload page (optional)
        history.pushState(null, null, '?page=' + page);
        
        // Load content
        loadContent(page);
    });

    // Handle browser back/forward buttons
    $(window).on('popstate', function() {
        var urlParams = new URLSearchParams(window.location.search);
        var page = urlParams.get('page') || 'dashboard';
        loadContent(page);
    });

    // Load default content berdasarkan URL atau default
    function loadInitialContent() {
        var urlParams = new URLSearchParams(window.location.search);
        var page = urlParams.get('page') || 'dashboard';
        loadContent(page);
    }

    // Load initial content
    loadInitialContent();
});