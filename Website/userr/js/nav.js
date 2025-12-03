// AJAX Navigation for Kedai Temeji
$(document).ready(function() {
    // Fungsi untuk memuat konten via AJAX
    function loadContent(page, params = {}) {
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
            data: { page: page, ...params },
            success: function(data) {
                $('#main-content').html(data);
                ceklog()
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
        
    }

    // Event handler untuk menu utama di sidebar
    $('.sidebar-menu').on('click', '.ajax-menu:not(.edit-user .btnBack)', function(e) {
        e.preventDefault();
        var page = $(this).data('page');
        
        updateActiveMenu($(this));
        history.pushState(null, null, '?page=' + page);
        loadContent(page);
    });

    
    // Event handler khusus untuk edit user di DALAM TABEL
    $(document).on('click', '.edit-user', function(e) {
        e.preventDefault();
        var page = $(this).data('page');
        var userId = $(this).data('user-id');
        var platform = $(this).data('platform');
        
        console.log('Edit user clicked:', page, userId, platform); // Debug log
        
        // Update active menu - tetap di list user karena kita masih di context yang sama
        var listPage = platform === 'website' ? 'user-list-web' : 'user-list-mobile';
        updateActiveMenu($('.sidebar-menu').find('[data-page="' + listPage + '"]').first());
        
        // Update URL
        history.pushState(null, null, '?page=' + page + '&id=' + userId);
        
        // Load content dengan parameter ID
        loadContent(page, { id: userId });
    });

    $(document).on('click', '.btnBack', function(e) {
        e.preventDefault();
        var page = $(this).data('page');
        history.pushState(null, null, '?page=' + page);
        // Load content
        loadContent(page);
    });

    // Fungsi untuk update menu aktif
    function updateActiveMenu($element) {
        $('.sidebar-menu li').removeClass('active');
        $element.closest('li').addClass('active');
        
        // Untuk treeview menu, set parent juga aktif
        var treeviewParent = $element.closest('.treeview-menu').closest('li');
        if (treeviewParent.length) {
            treeviewParent.addClass('active');
        }
    }

    // Handle browser back/forward buttons
    $(window).on('popstate', function() {
        var urlParams = new URLSearchParams(window.location.search);
        var page = urlParams.get('page') || 'dashboard';
        var id = urlParams.get('id') || '';
        
        var params = {};
        if (id) {
            params.id = id;
        }
        
        loadContent(page, params);
    });

    // Load default content berdasarkan URL atau default
    function loadInitialContent() {
        var urlParams = new URLSearchParams(window.location.search);
        var page = urlParams.get('page') || 'dashboard';
        var id = urlParams.get('id') || '';
        var params = {};
        if (id) {
            params.id = id;
        }
        
        loadContent(page, params);
    }

    // Load initial content
    loadInitialContent();
});

function ceklog(){
    var urlParams = new URLSearchParams(window.location.search); 
        for (const [key, value] of urlParams.entries()) {
  console.log(key, value);
}

}