$('#userInfoModal').on('click', '.toggle-group', function() {
    var toggle = $(this);
    var modal = $('#userInfoModal');
    var accountId = modal.data('id');
    var isActivated = modal.data('activated');

    setTimeout(function() {
        toggleActivation(accountId, !isActivated);
    }, 200);
});

$('#userInfoModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget);

    // Extract data from the button
    var id = button.data('id');
    var fullname = button.data('fullname');
    var email = button.data('email');
    var role = button.data('role');
    var phone = button.data('phone');
    var gender = button.data('gender');
    var address = button.data('address');
    var created = button.data('created');
    var activated = button.data('activated');
    var avatar = button.data('avatar');
    var displayName = button.data('displayname');

    switch(role) {
        case 'CUSTOMER':
            role = 'Khách hàng';
            break;
        case 'SELLER':
            role = 'Nhân viên bán hàng';
            break;
        case 'ADMIN':
            role = 'Quản trị viên';
            break;
        default:
            role = 'Khách hàng';
    }

    if (avatar === undefined || avatar === null || avatar === '') {
        switch (role) {
            case 'CUSTOMER':
                avatar = '/assets/images/customer.png';
                break;
            case 'SELLER':
                avatar = '/assets/images/seller.png';
                break;
            case 'ADMIN':
                avatar = '/assets/images/admin.png';
                break;
            default:
                avatar = '/assets/images/customer.png';
        }
    }


    var modal = $(this);
    modal.data('id', id);
    modal.data('activated', activated);
    modal.find('#display-name').text(displayName !== undefined ? displayName : fullname);
    modal.find('#user-fullname').text(fullname);
    modal.find('#user-email').text(email);
    modal.find('#user-role').text(role);
    modal.find('#user-phone').text(phone);
    modal.find('#user-gender').text(gender);
    modal.find('#user-address').text(address);
    modal.find('#user-created').text(created);
    if (activated) {
        modal.find('#user-activated').bootstrapToggle('on')
    } else {
    modal.find('#user-activated').bootstrapToggle('off')
    }

    modal.find('#avatar').attr('src', avatar);
});

function updateURL(order, role) {
    const url = new URL(window.location.href);
    const params = new URLSearchParams(url.search);

    const sortBy = document.querySelector('select[name="sortBy"]').value;
    const query = document.getElementById('account-management-search-input').value.trim();


    params.set('sortBy', sortBy);

    if(role !== undefined) {
        params.set('role', role);
    }

    if (order !== undefined) {
        params.set('order', order);
    }

    if (query) {
        params.set('query', query);
    } else {
        params.delete('query');
    }
    window.location.href = url.origin + url.pathname + '?' + params.toString();
}

function checkEnterKey(event) {
    if (event.key === 'Enter') {
        const sortAscButton = document.getElementById('sortAsc');
        const sortDescButton = document.getElementById('sortDesc');
        let order = 'desc';

        if (sortAscButton && sortAscButton.style.display !== 'none') {
            order = 'asc';
        } else if (sortDescButton && sortDescButton.style.display !== 'none') {
            order = 'desc';
        }

        updateURL(order);
    }
}

function toggleActivation(accountId, activate) {
    const url = '/admin/accounts/activate';
    const data = {
        id: accountId,
        activate: activate
    };

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Có lỗi xảy ra khi thực hiện thao tác.');
            }

            const currentUrl = new URL(window.location.href);

            currentUrl.searchParams.set('message', activate ? 'Activated' : 'Deactivated');

                window.location.href = currentUrl.toString();
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('error', 'Có lỗi xảy ra khi thực hiện thao tác.');
        });
}

document.addEventListener('DOMContentLoaded', function () {
    const urlParams = new URLSearchParams(window.location.search);
    const message = urlParams.get('message');

    if (message === 'Activated') {
        showToast('success', 'Tài khoản đã được kích hoạt thành công.');
    } else if (message === 'Deactivated') {
        showToast('success', 'Tài khoản đã được vô hiệu hóa thành công.');
    }

    if (message) {
        urlParams.delete('message');
        const newUrl = urlParams.toString() ? `${window.location.pathname}?${urlParams}` : window.location.pathname;
        window.history.replaceState({}, '', newUrl);
    }
});

