(function ($, document) {
    "use strict";

    $(document).on("click", ".custom-color-dropdown button", function () {
        
        const $select = $(this).closest("coral-select");

        setTimeout(function() {
            
            $select.find("coral-selectlist-item").each(function() {
                const colorClass = $(this).attr("value");
                
                if (colorClass) {
                    $(this).addClass(colorClass);
                }
            });
            
        }, 50); 
    });

})(jQuery, document);