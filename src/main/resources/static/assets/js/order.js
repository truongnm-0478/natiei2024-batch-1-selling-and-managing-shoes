const currentUrl = (window.location.pathname + window.location.search);
const navLinks = document.querySelectorAll('.list-group-item');

// Loop through each nav link
navLinks.forEach(link => {
    if (currentUrl.includes(link.getAttribute('href')))
        link.classList.add('active');
    else
        link.classList.remove('active');
});