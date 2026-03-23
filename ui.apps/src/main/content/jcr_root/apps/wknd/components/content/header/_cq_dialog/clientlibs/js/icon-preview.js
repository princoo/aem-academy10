(function ($, document, Coral) {
    "use strict";

    console.log("1. Icon script loaded successfully.");

    $(document).on("foundation-contentloaded", function (e) {
        
        var $iconDropdowns = $(".custom-icomoon-dropdown", e.target);
        console.log("2. Found custom dropdowns: ", $iconDropdowns.length);

        $iconDropdowns.each(function () {
            Coral.commons.ready(this, function(component) {
                
                $(component.items.getAll()).each(function() {
                    var iconClass = this.value; // e.g., 'icon-academy-search'
                    
                    // Make sure it has a value (skip "No Icon") and hasn't been injected yet
                    if (iconClass && $(this.content).find('.injected-icon').length === 0) {
                        
                        var iconHtml = '<i class="' + iconClass + ' injected-icon" style="margin-right: 10px; font-size: 16px;"></i>';
                        
                        // THE FIX: Use this.content.innerHTML instead of this.innerHTML
                        this.content.innerHTML = iconHtml + this.content.textContent;
                    }
                });
                
                console.log("3. Icons successfully injected!");
            });
        });
    });

})(jQuery, document, Coral);