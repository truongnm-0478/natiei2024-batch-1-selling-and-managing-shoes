var currentUrl = (window.location.pathname + window.location.search);
var navLinks = document.querySelectorAll('.list-group-item');

// Loop through each nav link
navLinks.forEach(link => {
    if (currentUrl.includes(link.getAttribute('href')))
        link.classList.add('active');
    else
        link.classList.remove('active');
});

var order_id
var order_status

function setData(orderId, orderStatus) {
    order_id = orderId
    order_status = orderStatus
}

function sendChangeStatusRequest(baseUrl) {
    const url = `${baseUrl}/${order_id}`;

    fetch(url, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({status: order_status}),
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.message || 'Something went wrong');
                });
            }
            return response.text();
        })
        .then(data => {
            document.open();
            document.write(data);
            document.close();
        })
        .catch(error => {
            console.error('Error:', error);
        })
        .finally(() => {
            order_id = null;
            order_status = null;
        });
}