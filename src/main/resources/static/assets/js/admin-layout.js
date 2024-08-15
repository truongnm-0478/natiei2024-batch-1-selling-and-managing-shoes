var sidebar = document.getElementById('sidebar');
var header = document.querySelector('.admin-header');
var mainContent = document.getElementById('admin-layout-main-content');
var toggleIcon = document.getElementById('toggleIconSidebar')
var adminLayoutWrapper = document.getElementById('admin-layout-wrapper')

document.getElementById('sidebarToggle').addEventListener('click', function () {

    sidebar.classList.toggle('collapsed');

    if (sidebar.classList.contains('collapsed')) {
        toggleIcon.src='/assets/images/icons/right-arrow-white.png'
        header.style.width = "calc(100% - 80px)";
        mainContent.style.width = 'calc(100% - 80px)';
        mainContent.style.marginLeft = '80px';
    } else {
        toggleIcon.src='/assets/images/icons/left-arrow-white.png'
        header.style.width = 'calc(100% - 250px)'; 
        mainContent.style.width = 'calc(100% - 250px)';
        mainContent.style.marginLeft = '250px';
    }
});
