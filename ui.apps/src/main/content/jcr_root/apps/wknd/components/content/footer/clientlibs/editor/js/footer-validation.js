(function ($, Coral) {
    "use strict";
    console.log("--- custom clientlibs loaded--")
    let registry = $(window).adaptTo("foundation-registry");
  
    // require validation for multifield max and min item
    registry.register("foundation.validation.validator", {
      selector: "[data-validation=footer-multifield-validation]",
      validate: function (element) {
        let el = $(element);
        let max = el.data("max-items");
        let min = el.data("min-items");
        let items = el.children("coral-multifield-item").length;
        console.log("{} {} ", max, min, items)
        if (items > max) {
          return (
            "You can add maximum " +
            max +
            " navigation option."
          );
  
        }
        if (items < min) {
          return "Require to add minimum " + min + " navigation option.";
        }
      },});
  })(jQuery, Coral);