(function($, Coral) {
    "use strict";

    $(document).on("dialog-ready", function() {
        
        var $numberField = $(".cq-dialog-slider-count");
        var $selectField = $(".cq-dialog-slider-select");

        if ($numberField.length === 0 || $selectField.length === 0) {
            return;
        }

        var selectElement = $selectField[0];

        function updateDropdown(count) {
            selectElement.items.clear();

            for (var i = 1; i <= count; i++) {
                var item = new Coral.Select.Item();
                item.content.textContent = "Slider " + i;
                item.value = i;
                selectElement.items.add(item);
            }
        }

        $numberField.on("change", function() {
            var count = parseInt($(this).val(), 10) || 0;
            updateDropdown(count);
        });

        var initialCount = parseInt($numberField.val(), 10) || 0;
        if (initialCount > 0) {
            updateDropdown(initialCount);
        }
    });

})( Granite.$, Coral );