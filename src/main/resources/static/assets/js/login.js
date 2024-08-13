document.getElementById('email').addEventListener('blur', validateEmailField);
document.getElementById('password').addEventListener('blur', validatePassword);

function validateEmailField() {
    const email = document.getElementById('email').value;
    const emailError = document.getElementById('email-error');
    if (!email) {
        emailError.textContent = 'Bạn cần nhập email';
    } else if (!validateEmail(email)) {
        emailError.textContent = 'Email không đúng định dạng';
    } else {
        emailError.textContent = '';
    }
}

function validatePassword() {
    const password = document.getElementById('password').value;
    const passwordError = document.getElementById('password-error');
    if (!password) {
        passwordError.textContent = 'Bạn cần nhập mật khẩu';
    } else if (password.length < 8) {
        passwordError.textContent = 'Mật khẩu phải lớn hơn 8 kí tự';
    } else {
        passwordError.textContent = '';
    }
}

function validateForm() {
    validateEmailField();
    validatePassword();

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

