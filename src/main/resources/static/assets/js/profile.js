console.log("==========================")
console.log(document.getElementById('gender').value)
document.getElementById('full_name').addEventListener('blur', validateFullName);
document.getElementById('display-name').addEventListener('blur', validateDisplayName);
document.getElementById('phone').addEventListener('blur', validatePhoneNumber);
document.getElementById('email').addEventListener('blur', validateEmailField);
document.getElementById('location').addEventListener('blur', validateAddress);
document.getElementById('datepicker').addEventListener('blur', validateDatepicker);
document.getElementById('password').addEventListener('blur', validatePassword);
document.getElementById('confirm_password').addEventListener('blur', validateConfirmPassword);

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
        confirmPasswordError.textContent = 'Xác thực mật khẩu không được để trống';
    } else if (password !== confirmPassword) {
        confirmPasswordError.textContent = 'Mật khẩu xác thực không khớp';
    } else {
        confirmPasswordError.textContent = '';
    }
}

function validateForm2() {
    validatePassword();
    validateConfirmPassword();

    const errors = document.querySelectorAll('.error-message');
    for (let i = 0; i < errors.length; i++) {
        if (errors[i].textContent !== '') {
            console.log(errors[i].textContent)
            return false;
        }
    }

    return true;
}

function validateFullName() {
    console.log("===========================")
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

function validateDisplayName() {
    const displayName = document.getElementById('display-name').value;
    const displayNameError = document.getElementById('displayNameError');
    if (!displayName) {
        displayNameError.textContent = 'Bạn cần nhập tên tài khoản';
    } else {
        displayNameError.textContent = '';
    }
}

function validatePhoneNumber() {
    const phoneNumber = document.getElementById('phone').value;
    const phoneNumberError = document.getElementById('phoneNumberError');
    if (!phoneNumber) {
        phoneNumberError.textContent = 'Bạn cần nhập số điện thoại';
    } else if (!/^\d{10}$/.test(phoneNumber)) {
        phoneNumberError.textContent = 'Số điện thoại phải gồm 10 chữ số';
    } else {
        phoneNumberError.textContent = '';
    }
}

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
    const address = document.getElementById('location').value;
    const addressError = document.getElementById('locationError');
    if (!address) {
        addressError.textContent = 'Bạn cần nhập địa chỉ';
    } else if (address.length < 10) {
        addressError.textContent = 'Địa chỉ cần phải lớn hơn 10 kí tự';
    } else {
        addressError.textContent = '';
    }
}

function validateDatepicker() {
    const datepicker = document.getElementById('datepicker').value;
    const datepickerError = document.getElementById('dateOfBirthError');
    const datePattern = /^\d{2}\/\d{2}\/\d{4}$/;
    if (!datepicker) {
        datepickerError.textContent = 'Ngày sinh không được để trống';
    } else if (!datePattern.test(datepicker)) {
        datepickerError.textContent = 'Ngày sinh phải có định dạng dd/MM/yyyy';
    } else {
        datepickerError.textContent = '';
    }
}

function validateEmail(email) {
    const re = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    return re.test(email);
}

function validateForm() {
    validateFullName();
    validateDisplayName();
    validatePhoneNumber();
    validateEmailField();
    validateAddress();
    // validateGender();
    validateDatepicker();

    const errors = document.querySelectorAll('.error-message');
    for (let i = 0; i < errors.length; i++) {
        if (errors[i].textContent !== '') {
            console.log(errors[i].textContent)
            return false;
        }
    }

    return true;
}

$(document).ready(function () {


    var readURL = function (input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();

            reader.onload = function (e) {
                $('.avatar').attr('src', e.target.result);
            }

            reader.readAsDataURL(input.files[0]);
        }
    }


    $(".file-upload").on('change', function () {
        readURL(this);
        $('#imageForm').submit(); // Tự động submit form sau khi chọn file
    });
});

