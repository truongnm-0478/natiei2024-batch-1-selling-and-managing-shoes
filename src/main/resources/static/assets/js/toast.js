function showToast(type, message) {
    console.log(type + ": " + message);

    // Tạo phần tử toast
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;

    // Tạo phần tử icon
    const icon = document.createElement('img');
    icon.className = 'toast-icon';
    icon.src = '/assets/images/icons/Logo_Ananas.png';

    // Tạo nội dung thông báo và nút tắt
    const toastContent = document.createElement('span');
    toastContent.innerText = message;

    const closeButton = document.createElement('button');
    closeButton.className = 'close-button';
    closeButton.innerHTML = '&times;'; // Ký tự '×' để hiển thị dấu nhân
    closeButton.onclick = () => {
        toast.classList.remove('show');
        // Xóa toast sau khi nó đã được ẩn
        setTimeout(() => {
            if (toast.parentElement) {
                toast.parentElement.removeChild(toast);
            }
            // Xóa container nếu không còn toast nào
            if (toastContainer && toastContainer.children.length === 0) {
                document.body.removeChild(toastContainer);
            }
        }, 300);
    };

    // Tạo thanh đếm thời gian tắt
    const progressBar = document.createElement('div');
    progressBar.className = 'progress-bar';

    // Thêm icon, nội dung và nút tắt vào toast
    toast.appendChild(icon);
    toast.appendChild(toastContent);
    toast.appendChild(closeButton);
    toast.appendChild(progressBar);

    // Tạo một container cho các toast nếu chưa có
    let toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container';
        document.body.appendChild(toastContainer);
    }

    // Thêm phần tử toast vào container
    toastContainer.appendChild(toast);

    // Hiển thị toast
    setTimeout(() => {
        toast.classList.add('show');
    }, 100);

    // Cập nhật thanh đếm thời gian
    let progressInterval;
    let width = 100; // Chiều rộng ban đầu của thanh tiến trình (100%)
    const duration = 10000; // Thời gian hiển thị toast (10 giây)
    const stepTime = duration / 100; // Khoảng thời gian mỗi lần cập nhật (tính cho 1% giảm đi)

    progressInterval = setInterval(() => {
        width--;
        progressBar.style.width = width + '%';
        if (width <= 0) {
            clearInterval(progressInterval);
        }
    }, stepTime);

    // Tự động ẩn toast sau một khoảng thời gian nếu người dùng không tắt
    setTimeout(() => {
        toast.classList.remove('show');
        // Xóa toast sau khi nó đã được ẩn
        setTimeout(() => {
            if (toast.parentElement) {
                toast.parentElement.removeChild(toast);
            }
            // Xóa container nếu không còn toast nào
            if (toastContainer && toastContainer.children.length === 0) {
                document.body.removeChild(toastContainer);
            }
        }, 300);
    }, duration);
}