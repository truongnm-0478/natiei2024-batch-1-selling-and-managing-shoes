document.addEventListener("DOMContentLoaded", function() {
    const sizeSelects = document.querySelectorAll('select[id^="select-size-"]');
    const quantityInputs = document.querySelectorAll('input[type="number"]');
    const allInputs = [...sizeSelects, ...quantityInputs];
    const loadingElement = document.getElementById('loading-filter');

    function showLoading() {
        loadingElement.style.display = 'block';
        allInputs.forEach(input => {
            input.disabled = true;
        });
    }

    function hideLoading() {
        loadingElement.style.display = 'none';
        allInputs.forEach(input => {
            input.disabled = false;
        });
    }

    function sendUpdateRequest(url, data, cartID) {
        const fetchPromise = fetch(url, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        const delayPromise = new Promise(resolve => setTimeout(resolve, 1000));

        return Promise.all([fetchPromise, delayPromise])
            .then(([fetchResponse]) => fetchResponse.json())
            .then(json => {
                if (json.reload) {
                    location.reload();
                } else {
                    document.getElementById('cart-total').textContent = json.totalOriginPrice;
                    document.getElementById('cart-total-discount').textContent = json.totalDiscountedPrice;
                    document.getElementById('cart-final-price').textContent = json.finalPrice;
                    var priceElement = document.getElementById(`total-price-${cartID}`);
                    var price = parseInt(priceElement.getAttribute('data-price'), 10);
                    var totalPrice = price * data.quantity;
                    priceElement.textContent = totalPrice.toLocaleString('vi-VN', {
                        style: 'currency',
                        currency: 'VND'
                    });

                }
                showToast('success', json.message);

            })
            .catch(error => {
                showToast('error', 'Có lỗi xảy ra khi cập nhật giỏ hàng');
            });
    }

    sizeSelects.forEach(sizeSelect => {
        const index = sizeSelect.id.split('-').pop();
        const quantityInput = document.getElementById(`select-quantity-${index}`);
        const cartID = parseInt(sizeSelect.getAttribute('data-cart-id'), 10);

        sizeSelect.addEventListener('change', function() {
            const selectedOption = this.options[this.selectedIndex];
            const maxQuantity = parseInt(selectedOption.getAttribute('data-quantity'), 10);
            const quantityID = parseInt(selectedOption.getAttribute('data-quantity-id'), 10);

            if (!isNaN(maxQuantity) && maxQuantity > 0) {
                quantityInput.disabled = false;
                quantityInput.max = maxQuantity;
                quantityInput.min = 1;
                quantityInput.value = Math.min(parseInt(quantityInput.value, 10) || 1, maxQuantity);
            } else {
                quantityInput.disabled = true;
                quantityInput.max = 1;
                quantityInput.min = 1;
                quantityInput.value = 1;
            }

            showLoading();

            const data = {
                quantity: parseInt(quantityInput.value, 10),
                productQuantityId: quantityID,
                action: "size-change"
            };

            sendUpdateRequest(`/carts/${cartID}/edit`, data, cartID)
                .finally(() => {
                    hideLoading();
                });
        });

        // Initialize default values
        const defaultSize = sizeSelect.getAttribute('value');
        const quantityOptions = sizeSelect.querySelectorAll('option');
        quantityOptions.forEach(option => {
            if (option.value === defaultSize) {
                sizeSelect.value = defaultSize;
                const maxQuantity = parseInt(option.getAttribute('data-quantity'), 10);
                if (!isNaN(maxQuantity) && maxQuantity > 0) {
                    quantityInput.disabled = false;
                    quantityInput.max = maxQuantity;
                    quantityInput.min = 1;
                    quantityInput.value = Math.min(parseInt(quantityInput.value, 10) || 1, maxQuantity);
                } else {
                    quantityInput.disabled = true;
                    quantityInput.max = 1;
                    quantityInput.min = 1;
                    quantityInput.value = 1;
                }
            }
        });
    });

    quantityInputs.forEach(quantityInput => {
        const index = quantityInput.id.split('-').pop();
        const sizeSelect = document.getElementById(`select-size-${index}`);
        const cartID = parseInt(sizeSelect.getAttribute('data-cart-id'), 10);

        quantityInput.addEventListener('input', debounce(function() {
            const sizeValue = sizeSelect.value;
            const maxQuantity = parseInt(this.max, 10);
            const minQuantity = parseInt(this.min, 10);
            let value = parseInt(this.value, 10) || 1;

            if (value < minQuantity) value = minQuantity;
            if (value > maxQuantity) value = maxQuantity;

            this.value = value;

            showLoading();

            const data = {
                quantity: value,
                productQuantityId: parseInt(sizeSelect.querySelector('option:checked').getAttribute('data-quantity-id'), 10),
                action: "quantity-change",
            };

            sendUpdateRequest(`/carts/${cartID}/edit`, data, cartID)
                .finally(() => {
                    hideLoading();
                });
        }, 500));
    });
});

function debounce(func, delay) {
    let timeout;
    return function(...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), delay);
    };
}