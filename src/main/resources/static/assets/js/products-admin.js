document.addEventListener("DOMContentLoaded", function () {
    const productStyleSelect = document.getElementById("product-style-select");
    const productMaterialSelect = document.getElementById("product-material-select");
    const productSearchInput = document.getElementById("product-management-search-input");
    const confirmButton = document.getElementById("confirmDeleteButton");
    const cancelButton = document.querySelector("[data-dismiss='modal']");

    let productIdToDelete = null;

    function getCurrentPage() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get("page") || 1; // Default to page 1 if 'page' param is not present
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
        } else {
            queryParams.push(`page=${currentPage}`);
        }

        const queryString = queryParams.length ? `?${queryParams.join("&")}` : "";
        console.log(queryString);
        window.location.href = `/seller/products${queryString}`;
    }

    productStyleSelect.addEventListener("change", applyFilters);
    productMaterialSelect.addEventListener("change", applyFilters);
    productSearchInput.addEventListener("keypress", function (e) {
        if (e.key === "Enter") {
            applyFilters();
        }
    });

    document.querySelectorAll(".pm-card-delete-button").forEach((button) => {
        button.addEventListener("click", function (event) {
            event.stopPropagation();
            event.preventDefault();

            // Store the product ID to be deleted
            productIdToDelete = this.getAttribute("data-product-id");

            // Show the modal
            const modal = document.getElementById("cancelConfirmModal");
            modal.classList.add("show");
            modal.style.display = "block";
        });
    });

    confirmButton.addEventListener("click", function () {
        if (productIdToDelete) {
            deleteProduct(productIdToDelete);
        }
    });

    cancelButton.addEventListener("click", function () {
        const modal = document.getElementById("cancelConfirmModal");
        modal.classList.remove("show");
        modal.style.display = "none";
    });

    function deleteProduct(productId) {
        fetch(`/seller/products/${productId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(()=>{
            window.location.href = window.location.href;
        }) 
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while deleting the product.');
        });        
    }
});
