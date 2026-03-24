(function ($, document, Coral) {
    "use strict";

    $(document).on("foundation-contentloaded", function (e) {
        
        var $iconDropdowns = $(".custom-icomoon-dropdown", e.target);

        $iconDropdowns.each(function () {
            Coral.commons.ready(this, function(component) {
                
                $(component.items.getAll()).each(function() {
                    var iconClass = this.value;
                    
                    if (iconClass && $(this.content).find('.injected-icon').length === 0) {
                        
                        var iconHtml = '<i class="' + iconClass + ' injected-icon" style="margin-right: 10px; font-size: 16px;"></i>';
                        
                        this.content.innerHTML = iconHtml + this.content.textContent;
                    }
                });
                
            });
        });
    });

})(jQuery, document, Coral);