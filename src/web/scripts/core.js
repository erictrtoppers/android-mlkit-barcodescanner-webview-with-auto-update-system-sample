var lastBarcodeField = null;
var isAndroidDevice = typeof app != typeof undefined;
var isWeb = typeof app == typeof undefined;
var disableScalingCSS = false;

$(document).ready(function () {
    // Bind DOM
    bind();

    // Make sure it's set correctly
    isAndroidDevice = typeof app != typeof undefined;
    isWeb = typeof app == typeof undefined;
});

function bind() {
    $(document.body).on('focus', '.barcode', function (e) {
        if (typeof app != typeof undefined) {
            lastBarcodeField = $(this);
            app.initBarcodeScan();
        }
    });

    var disScalCSS = localStorage.getItem('disScalCSS');
    if (disScalCSS && disScalCSS === "true") {
        disableScalingCSS = true;
        $('link.deviceScalingOverridesSS').prop('disabled', true);
    }
}

function processBarcodeValue(barcodeValue) {
    if (lastBarcodeField && lastBarcodeField.length && barcodeValue) {
        lastBarcodeField.val(barcodeValue);
        lastBarcodeField.trigger('change');
        lastBarcodeField.blur();
        lastBarcodeField = null;
    }
}