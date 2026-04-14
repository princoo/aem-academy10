(function ($, document) {
    "use strict";

    $(document).on("dialog-ready", function () {
        const $select = $(".cq-dialog-banner-active-select");
        if (!$select.length) return;

        function syncDropdown() {
            const selectElement = $select.get(0);
            let activeSlides = [];

            for (let i = 1; i <= 6; i++) {
                const isHidden = $(`.cq-dialog-banner-hide-checkbox[name="./hideSlide${i}"]`).find('input[type="checkbox"]').is(':checked');
                const orderValue = Number.parseInt($(`.cq-dialog-banner-order-field[name="./slide${i}Order"]`).val()) || i;

                if (!isHidden) {
                    activeSlides.push({
                        label: `Slide ${i}`,
                        order: orderValue
                    });
                }
            }

            activeSlides.sort((a, b) => a.order - b.order);

            selectElement.items.clear(); 

            activeSlides.forEach((slide, visualIndex) => {
                selectElement.items.add({
                    content: { innerHTML: `Pos ${visualIndex + 1}: ${slide.label}` },
                    value: visualIndex.toString()
                });
            });
        }

        $(document).on("change", ".cq-dialog-banner-hide-checkbox, .cq-dialog-banner-order-field", function() {
            syncDropdown();
        });

        setTimeout(syncDropdown, 200);
    });

})(Granite.$, document);