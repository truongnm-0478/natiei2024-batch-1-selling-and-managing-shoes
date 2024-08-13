let provinces = [];

document.getElementById('full_name').addEventListener('blur', validateFullName);
document.getElementById('phone_number').addEventListener('blur', validatePhoneNumber);
document.getElementById('email').addEventListener('blur', validateEmailField);
document.getElementById('address').addEventListener('blur', validateAddress);
document.getElementById('province').addEventListener('blur', validateProvince);
document.getElementById('district').addEventListener('blur', validateDistrict);
document.getElementById('ward').addEventListener('blur', validateWard);

document.getElementById('cb2').addEventListener('change', function () {
    this.checked = true;
});

document.getElementById('cb3').addEventListener('change', function () {
    this.checked = true;
});

document.getElementById('cb5').addEventListener('change', function () {
    this.checked = true;
});

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

function validateProvince() {
    const province = document.getElementById('province').value;
    const provinceError = document.getElementById('provinceError');
    if (!province) {
        provinceError.textContent = 'Tỉnh/ Thành phố không được để trống';
    } else {
        provinceError.textContent = '';
    }
}

function validateDistrict() {
    const district = document.getElementById('district').value;
    const districtError = document.getElementById('districtError');
    if (!district) {
        districtError.textContent = 'Quận/ Huyện không được để trống';
    } else {
        districtError.textContent = '';
    }
}

function validateWard() {
    const ward = document.getElementById('ward').value;
    const wardError = document.getElementById('wardError');
    if (!ward) {
        wardError.textContent = 'Phường/ Xã không được để trống';
    } else {
        wardError.textContent = '';
    }
}

function validateForm() {
    validateFullName();
    validatePhoneNumber();
    validateEmailField();
    validateAddress();
    validateProvince();
    validateDistrict();
    validateWard();

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

// Hàm để lấy dữ liệu từ tệp JSON
async function fetchData(url) {
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        return data;
    } catch (error) {
        console.error("Fetch error: ", error);
    }
}

// Hàm bất đồng bộ để khởi tạo dữ liệu và cập nhật DOM
async function initialize() {
    const url =
        "https://gist.githubusercontent.com/giathinh910/5e0ad51b7356a781dbaf4dbac802a3d5/raw/88e50434eef35bbd5d5cf426870901d8ee8196ff/vietnam-provinces.json";
    provinces = await fetchData(url);

    const provinceSelect = document.getElementById("province");

    // Cập nhật các tỉnh/thành phố sau khi dữ liệu đã được tải
    if (provinces) {
        provinces.forEach((province) => {
            let option = document.createElement("option");
            option.value = province.name;
            option.text = province.name;
            provinceSelect.add(option);
        });
    }
}

// Hàm để cập nhật các quận/huyện
function updateDistricts() {
    const provinceSelect = document.getElementById("province");
    const districtSelect = document.getElementById("district");
    const wardSelect = document.getElementById("ward");
    const selectedProvince = provinceSelect.value;

    // Xóa tất cả các tùy chọn quận/huyện hiện có
    districtSelect.innerHTML = '<option value="">Chọn Quận/Huyện</option>';
    wardSelect.innerHTML = '<option value="">Chọn Phường/Xã</option>'; // Xóa tùy chọn phường/xã

    if (selectedProvince) {
        const province = provinces.find((p) => p.name === selectedProvince);
        if (province) {
            province.districts.forEach((district) => {
                let option = document.createElement("option");
                option.value = district.name;
                option.text = district.name;
                districtSelect.add(option);
            });
        }
    }
}

// Hàm để cập nhật các phường/xã
function updateWards() {
    const provinceSelect = document.getElementById("province");
    const districtSelect = document.getElementById("district");
    const wardSelect = document.getElementById("ward");
    const selectedProvince = provinceSelect.value;
    const selectedDistrict = districtSelect.value;

    // Xóa tất cả các tùy chọn phường/xã hiện có
    wardSelect.innerHTML = '<option value="">Chọn Phường/Xã</option>';

    if (selectedProvince && selectedDistrict) {
        const province = provinces.find((p) => p.name === selectedProvince);
        if (province) {
            const district = province.districts.find(
                (d) => d.name === selectedDistrict
            );
            if (district) {
                district.wards.forEach((ward) => {
                    let option = document.createElement("option");
                    option.value = ward.name;
                    option.text = ward.name;
                    wardSelect.add(option);
                });
            }
        }
    }
}

// Gọi hàm initialize sau khi trang được tải
document.addEventListener("DOMContentLoaded", initialize);


