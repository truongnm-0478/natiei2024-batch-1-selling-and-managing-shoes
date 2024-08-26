var monthlyRevenueChart, yearlyRevenueChart;

$(document).ready(function () {
    // Khởi tạo biểu đồ doanh thu tháng và năm
    monthlyRevenueChart = initializeChart('monthlyRevenueChart', 'line', 'Biểu đồ doanh thu tháng');
    yearlyRevenueChart = initializeChart('yearlyRevenueChart', 'doughnut', 'Biểu đồ doanh thu năm');

    // Xử lý sự kiện khi người dùng thay đổi tháng
    $('#date-picker').on('change', function () {
        fetchRevenueData($(this).val());
    });

    // Khởi tạo biểu đồ và bảng lần đầu tiên
    fetchRevenueData($('#date-picker').val());
});

// Khởi tạo biểu đồ chung
function initializeChart(chartId, type, title) {
    const ctx = document.getElementById(chartId).getContext('2d');
    return new Chart(ctx, {
        type: type,
        data: {
            labels: [],
            datasets: [{
                label: 'Doanh thu',
                data: [],
                borderColor: '#3e95cd',
                backgroundColor: type === 'doughnut' ? [
                    '#e6194b', '#3cb44b', '#ffe119', '#4363d8', '#f58231', '#911eb4', '#46f0f0', '#f032e6',
                    '#bcf60c', '#fabebe', '#008080', '#e6beff'
                ] : [],
                fill: type !== 'line'
            }]
        },
        options: {
            responsive: true,
            title: {
                display: true,
                text: title
            }
        }
    });
}

// Hàm fetch dữ liệu doanh thu
function fetchRevenueData(date) {
    $.get('/admin/statistic/data', { date: date }, function (response) {
        updatePageContent(response);
    }).fail(function (error) {
        console.error('Error fetching revenue data:', error);
    });
}

// Cập nhật toàn bộ nội dung trang khi nhận được dữ liệu mới
function updatePageContent(data) {
    // Cập nhật các thẻ h3
    $('#orderCount').text(data.revenueInfo.orderCount);
    $('#currentRevenue').text(formatCurrency(data.revenueInfo.currentRevenue));
    $('#predictedRevenue').text(formatCurrency(data.revenueInfo.predictedRevenue));

    // Cập nhật bảng số liệu doanh thu hàng ngày
    updateTableContent('#dailyRevenueTable', data.dailyRevenue, 'date');
    updateChartData(monthlyRevenueChart, Object.keys(data.dailyRevenue), Object.values(data.dailyRevenue).map(info => info.currentRevenue));

    // Cập nhật bảng số liệu doanh thu hàng tháng
    updateTableContent('#monthlyRevenueTable', data.monthlyRevenue, 'month');
    updateChartData(yearlyRevenueChart, Object.keys(data.monthlyRevenue), Object.values(data.monthlyRevenue).map(info => info.currentRevenue));

    // Cập nhật tổng doanh thu
    $('#totalMonthlyRevenue').text(formatCurrency(getTotalRevenue(data.dailyRevenue)));
    $('#totalYearlyRevenue').text(formatCurrency(getTotalRevenue(data.monthlyRevenue)));
}

// Hàm cập nhật nội dung bảng
function updateTableContent(tableId, data, dateKey) {
    const tableBody = $(tableId).empty();
    $.each(data, function (key, info) {
        tableBody.append(`<tr>
                        <td>${key}</td>
                        <td>${info.orderCount}</td>
                        <td>${formatCurrency(info.currentRevenue)}</td>
                    </tr>`);
    });
}

// Hàm cập nhật dữ liệu biểu đồ
function updateChartData(chart, labels, data) {
    chart.data.labels = labels;
    chart.data.datasets[0].data = data;
    chart.update();
}

// Hàm tính tổng doanh thu
function getTotalRevenue(data) {
    return Object.values(data).reduce((acc, info) => acc + info.currentRevenue, 0);
}

// Hàm định dạng tiền tệ
function formatCurrency(value) {
    return value.toLocaleString('vi-VN', {
        style: 'currency',
        currency: 'VND'
    });
}