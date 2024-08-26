var modal = document.getElementById("productModal");
var btn = document.querySelector(".product-management-add-button");
var span = document.getElementsByClassName("close")[0];
var btnSubmit = document.getElementById("btn-submit");

btn.onclick = function() {
    modal.style.display = "block";
}

span.onclick = function() {
    modal.style.display = "none";
}

window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}

btnSubmit.onclick = function() {
    modal.style.display = "none";
}