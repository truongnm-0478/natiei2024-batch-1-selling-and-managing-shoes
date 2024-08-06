document.getElementById('email').addEventListener('blur', validateEmailField);
document.getElementById('address').addEventListener('blur', validateAddress);
document.getElementById('password').addEventListener('blur', validatePassword);
document.getElementById('confirm_password').addEventListener('blur', validateConfirmPassword);
document.getElementById('full_name').addEventListener('blur', validateFullName);
document.getElementById('phone_number').addEventListener('blur', validatePhoneNumber);

function validateEmailField() {
    const email = document.getElementById('email').value;
    const emailError = document.getElementById('emailError');
    if (!email) {
        emailError.textContent = 'Bạn cần nhập email';
    } else if (!validateEmail(email)) {
        emailError.textContent = 'Email không đúng định dạng';
    } else {
        emailError.textContent = '';
    }
}

function validateAddress() {
    const address = document.getElementById('address').value;
    const addressError = document.getElementById('addressError');
    if (!address) {
        addressError.textContent = 'Bạn cần nhập địa chỉ';
    } else if (address.length < 10) {
        addressError.textContent = 'Địa chỉ cần phải lớn hơn 10 kí tự';
    } else {
        addressError.textContent = '';
    }
}

function validatePassword() {
    const password = document.getElementById('password').value;
    const passwordError = document.getElementById('passwordError');
    if (!password) {
        passwordError.textContent = 'Bạn cần nhập mật khẩu';
    } else if (password.length < 8) {
        passwordError.textContent = 'Mật khẩu phải lớn hơn 8 kí tự';
    } else {
        passwordError.textContent = '';
    }
}

function validateConfirmPassword() {
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm_password').value;
    const confirmPasswordError = document.getElementById('confirmPasswordError');
    if (!confirmPassword) {
        confirmPasswordError.textContent = 'Bạn cần nhập lại mật khẩu';
    } else if (password !== confirmPassword) {
        confirmPasswordError.textContent = 'Mật khẩu nhập lại không trùng khớp';
    } else {
        confirmPasswordError.textContent = '';
    }
}

function validateFullName() {
    const fullName = document.getElementById('full_name').value;
    const fullNameError = document.getElementById('fullNameError');
    if (!fullName) {
        fullNameError.textContent = 'Bạn cần nhập họ tên';
    } else if (fullName.length < 8) {
        fullNameError.textContent = 'Họ tên cần lớn hơn 8 kí tự';
    } else {
        fullNameError.textContent = '';
    }
}

function validatePhoneNumber() {
    const phoneNumber = document.getElementById('phone_number').value;
    const phoneNumberError = document.getElementById('phoneNumberError');
    if (!phoneNumber) {
        phoneNumberError.textContent = 'Bạn cần nhập số điện thoại';
    } else if (!/^\d{10}$/.test(phoneNumber)) {
        phoneNumberError.textContent = 'Số điện thoại phải gồm 10 chữ số';
    } else {
        phoneNumberError.textContent = '';
    }
}

function validateForm() {
    validateEmailField();
    validateAddress();
    validatePassword();
    validateConfirmPassword();
    validateFullName();
    validatePhoneNumber();

    const errors = document.querySelectorAll('.error-message');
    for (let i = 0; i < errors.length; i++) {
        if (errors[i].textContent !== '') {
            return false;
        }
    }

    return true;
}

function validateEmail(email) {
    const re = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    return re.test(email);
}