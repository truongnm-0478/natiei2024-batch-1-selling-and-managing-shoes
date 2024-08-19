document.addEventListener("DOMContentLoaded", function () {
    const styleList = [];
    const categoryList = [];
    const materialList = [];
    const colorList = [];
    let gender = 1; // Mặc định là "TẤT CẢ"

    const updateFilters = (list, value) => {
        const index = list.indexOf(value);
        if (index > -1) {
            list.splice(index, 1);
        } else {
            list.push(value);
        }
        return list.join(", ");
    };

    // Khởi tạo danh sách các phần tử đã active trước đó
    document.querySelectorAll(".cb-item").forEach((item) => {
        const label = item.closest("label");
        if (label.classList.contains("active")) {
            const value = item.value;
            if (item.name === "cbStyle") {
                styleList.push(value);
            } else if (item.name === "cbCategory") {
                categoryList.push(value);
            } else if (item.name === "cbMaterial") {
                materialList.push(value);
            } else if (item.name === "cbColor") {
                colorList.push(value);
            }
        }
    });

    console.log("Initial Styles: ", styleList.join(", "));
    console.log("Initial Categories: ", categoryList.join(", "));
    console.log("Initial Materials: ", materialList.join(", "));
    console.log("Initial Colors: ", colorList.join(", "));

    // Thêm sự kiện click cho các phần tử checkbox
    document.querySelectorAll(".cb-item").forEach((item) => {
        item.addEventListener("click", function () {
            const value = this.value;
            const label = this.closest("label");  // Tìm thẻ label gần nhất chứa phần tử input

            if (this.name === "cbStyle") {
                label.classList.toggle("active");
                console.log("Selected Styles: ", updateFilters(styleList, value));
            } else if (this.name === "cbCategory") {
                label.classList.toggle("active");
                console.log("Selected Categories: ", updateFilters(categoryList, value));
            } else if (this.name === "cbMaterial") {
                label.classList.toggle("active");
                console.log("Selected Materials: ", updateFilters(materialList, value));
            } else if (this.name === "cbColor") {
                label.classList.toggle("active");
                console.log("Selected Colors: ", updateFilters(colorList, value));
            }

            const params = new URLSearchParams({
                filterStyleString: styleList.join(','),  // Chuyển đổi danh sách thành chuỗi
                filterCategoryString: categoryList.join(','),
                filterMaterialString: materialList.join(','),
                filterColorString: colorList.join(','),
                gender: gender  // Thêm tham số gender
            });

            window.location.href = `/products?${params}`;
        });
    });

    // Thêm sự kiện click cho các tab
    document.querySelectorAll(".nav-tabs a").forEach((tab) => {
        tab.addEventListener("click", function () {
            gender = this.getAttribute("data-gender");  // Lấy giá trị gender từ thuộc tính data-gender
            console.log("Selected Gender: ", gender);

            const params = new URLSearchParams({
                filterStyleString: styleList.join(','),  // Chuyển đổi danh sách thành chuỗi
                filterCategoryString: categoryList.join(','),
                filterMaterialString: materialList.join(','),
                filterColorString: colorList.join(','),
                gender: gender  // Thêm tham số gender
            });

            window.location.href = `/products?${params}`;
        });
    });
});
