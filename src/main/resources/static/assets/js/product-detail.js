function changeImage(imageUrl) {
    document.getElementById('main-image').src = imageUrl;
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