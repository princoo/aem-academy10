(function(document, $, Granite) {
    "use strict";

    $(document).on("click", "#wknd-import-btn", function(e) {
        e.preventDefault();

        // 1. Get the 'foundation-ui' object
        var ui = $(window).adaptTo("foundation-ui");

        // 2. Grab the values
        // Note: Granite PathFields usually store the value in an input with the 'name' attribute
        var csv = $("[name='csvPath']").val();
        var parent = $("[name='parentPath']").val();

        console.log("DEBUG: CSV Path ->", csv);
        console.log("DEBUG: Parent Path ->", parent);

        // 3. Validation using the 'ui' object instead of Granite.UI.Alert
        if (!csv || !parent) {
            ui.alert("Selection Missing", "Please select both a CSV file and a destination folder.", "error");
            return;
        }

        // 4. Show the loading spinner
        ui.wait();

        // 5. Trigger the Servlet
        $.ajax({
            url: "/bin/wknd/import-courses",
            type: "GET",
            data: {
                csvPath: csv,
                parentPath: parent
            },
            success: function(response) {
                ui.clearWait();
                ui.alert("Import Success", response, "success");
            },
            error: function(xhr) {
                ui.clearWait();
                ui.alert("Import Failed", "Status: " + xhr.status + " - Check AEM logs for details.", "error");
            }
        });
    });
})(document, Granite.$, Granite);