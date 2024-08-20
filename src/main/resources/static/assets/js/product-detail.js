function changeImage(imageUrl) {
    document.getElementById('main-image').src = imageUrl;
}

function updateMaxQuantity() {
    const sizeSelect = document.getElementById('size');
    const quantityInput = document.getElementById('quantity');
    const addToCartButton = document.getElementById('add-to-cart-button');
    const selectedOption = sizeSelect.options[sizeSelect.selectedIndex];
    const maxQuantity = selectedOption.getAttribute('data-max-quantity');

    if (maxQuantity === "0") {
        quantityInput.min = 0;
        quantityInput.max = 0;
        quantityInput.value = 0;
        quantityInput.disabled = true;
        addToCartButton.disabled = true;
    } else {
        quantityInput.min = 1;
        quantityInput.value = 1;
        quantityInput.max = maxQuantity;
        quantityInput.disabled = false;
        addToCartButton.disabled = false;
    }
}

var swiper = new Swiper('.swiper-container', {
    slidesPerView: 4,
    spaceBetween: 10,
    navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
    },
});

document.addEventListener('DOMContentLoaded', function () {
    updateMaxQuantity();

    const updateTextColors = () => {
        document.querySelectorAll('[data-toggle="collapse"]').forEach(toggleElement => {
            const isExpanded = toggleElement.getAttribute('aria-expanded') === 'true';
            const span = toggleElement.querySelector('span');
            if (span) {
                span.style.color = isExpanded ? 'var(--color-orange-light)' : 'black';
            }
        });
    };

    // Initial check
    updateTextColors();

    // Listen for collapse events
    document.querySelectorAll('[data-toggle="collapse"]').forEach(element => {
        element.addEventListener('click', () => {
            setTimeout(updateTextColors, 300);
        });
    });

});