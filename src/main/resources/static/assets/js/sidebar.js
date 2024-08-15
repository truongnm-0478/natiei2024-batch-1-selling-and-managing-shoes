document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.tree-toggle.nav-header.orange').forEach(header => {
        // Truy cập vào phần tử cha của class `orange`
        const navHeader = header.closest('.nav-header');
        // Nếu phần tử cha có class `orange` không tồn tại, bỏ qua
        if (!navHeader) return;

        header.addEventListener('click', function () {
            // Đổi màu chữ và icon tương ứng khi trạng thái class="nav tree" là show và hide
            const tree = this.nextElementSibling;
            const icon = this.querySelector('.caret');

            if (tree && tree.classList.contains('show')) {
                tree.classList.remove('show');
                tree.classList.add('hide');
                header.style.color = '#f15e2c'; // Màu cam
                icon.classList.remove('caret-active');
                icon.classList.add('caret-normal');
            } else if (tree) {
                tree.classList.remove('hide');
                tree.classList.add('show');
                header.style.color = '#000'; // Màu đen
                icon.classList.remove('caret-normal');
                icon.classList.add('caret-active');
            }
        });
    });

    // Lắng nghe sự kiện click vào các `label` trong `li`
    document.querySelectorAll('.tree li label').forEach(label => {
        label.addEventListener('click', function () {
            const checkbox = this.querySelector('input[type="checkbox"]');
            if (checkbox) {
                checkbox.checked = !checkbox.checked;

                if (checkbox.checked) {
                    this.classList.add('cb-checked');
                    label.style.backgroundColor = '#f1f1f1'; // Thay đổi background-color
                    const glyphicon = this.querySelector('.glyphicon');
                    if (glyphicon) {
                        glyphicon.style.display = 'inline-block';
                    }
                } else {
                    this.classList.remove('cb-checked');
                    label.style.backgroundColor = '#fff'; // Gỡ bỏ background-color
                    const glyphicon = this.querySelector('.glyphicon');
                    if (glyphicon) {
                        glyphicon.style.display = 'none';
                    }
                }
            }
        });
    });

    // Lắng nghe sự kiện click vào các `span` có class="bg-color"
    document.querySelectorAll('.bg-color').forEach(span => {
        span.addEventListener('click', function () {
            const parentLabel = this.closest('label');
            const cbColorCheckedClass = 'cb-color-checked';

            if (parentLabel) {
                if (parentLabel.classList.contains(cbColorCheckedClass)) {
                    parentLabel.classList.remove(cbColorCheckedClass);
                } else {
                    parentLabel.classList.add(cbColorCheckedClass);
                }
            }
        });
    });
};