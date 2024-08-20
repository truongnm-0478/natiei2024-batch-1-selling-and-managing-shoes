document.addEventListener("DOMContentLoaded", function () {
    const productStyleSelect = document.getElementById("product-style-select");
    const productMaterialSelect = document.getElementById("product-material-select");
    const productSearchInput = document.getElementById("product-management-search-input");

    function getCurrentPage() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('page') || 1; // Nếu không có tham số 'page', mặc định là trang 1
    }

    function applyFilters() {
        const style = productStyleSelect.value;
        const material = productMaterialSelect.value;
        const searchQuery = productSearchInput.value;
        const currentPage = getCurrentPage();

        let queryParams = [];

        queryParams.push(`filterMaterial=${material}`);
        queryParams.push(`filterStyle=${style}`);

        if (searchQuery) {
            queryParams.push(`queryProduct=${searchQuery}`);
        }else {
            queryParams.push(`page=${currentPage}`);
        }

        const queryString = queryParams.length ? `?${queryParams.join('&')}` : '';
        console.log(queryString)
        window.location.href = `/seller/products${queryString}`;
    }

    productStyleSelect.addEventListener("change", applyFilters);
    productMaterialSelect.addEventListener("change", applyFilters);
    productSearchInput.addEventListener("keypress", function (e) {
        if (e.key === 'Enter') {
            applyFilters();
        }
    });
});
