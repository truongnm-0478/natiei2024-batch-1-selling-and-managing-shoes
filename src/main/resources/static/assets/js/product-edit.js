let imagesToAdd = [];
let imagesToRemove = [];

$(document).ready(function () {
    $('#file').change(function (event) {
        const input = event.target;
        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function (e) {
                const newImageHtml = `
                        <div class="img-item mr-2" data-new-image="${e.target.result}">
                            <div class="close">
                                <div class="line one"></div>
                                <div class="line two"></div>
                            </div>
                            <img src="${e.target.result}" alt="">
                        </div>`;
                $('#image-gallery').append(newImageHtml);
                imagesToAdd.push(input.files[0]);
            };
            reader.readAsDataURL(input.files[0]);
        }
    });

    $(document).on('click', '.close', function () {
        const imgItem = $(this).closest('.img-item');
        const imageId = imgItem.data('image-id');
        const newImage = imgItem.data('new-image');

        if (imageId) {
            imagesToRemove.push(imageId);
        }

        if (newImage) {
            imagesToAdd = imagesToAdd.filter(img => img !== newImage);
        }

        imgItem.remove();
    });

    $('#submit-button').on('click', function () {
        $('#confirmConfirmModal').modal('show');
        $('#confirmConfirmModal').data('form', '#product-edit-form');
    });

    $('#product-detail-edit-form').on('submit', function (event) {
        event.preventDefault();
        $('#confirmConfirmModal').modal('show');
        $('#confirmConfirmModal').data('form', '#product-detail-edit-form');
    });

    $('#confirm-save-changes').on('click', function () {
        const formId = $('#confirmConfirmModal').data('form');
        const form = $(formId);

        if (formId === '#product-edit-form') {
            // Append new images to the form
            imagesToAdd.forEach((image, index) => {
                let input = document.createElement('input');
                input.type = 'hidden';
                input.name = `imagesToAdd[${index}]`;
                input.value = image;
                form.append(input);
            });

            // Append images to remove to the form
            let removeInput = document.createElement('input');
            removeInput.type = 'hidden';
            removeInput.name = 'imagesToRemove';
            removeInput.value = JSON.stringify(imagesToRemove);
            form.append(removeInput);
        }

        // Submit the form
        form.off('submit').submit();
    });
});