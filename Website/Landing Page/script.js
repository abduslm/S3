// Mobile menu functionality (guard in case button not present)
const _mobileBtn = document.querySelector('.mobile-menu-btn');
if (_mobileBtn) {
    _mobileBtn.addEventListener('click', function() {
        const nav = document.querySelector('.navbar');
        if (nav) nav.classList.toggle('active');
    });
}