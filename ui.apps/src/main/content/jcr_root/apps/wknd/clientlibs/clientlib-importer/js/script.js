(function(document, $, Granite) {
    "use strict";

    var ui = $(window).adaptTo("foundation-ui");
    
    function startProgressMonitoring(parentPath) {
        var $wrapper = $("#wknd-progress-wrapper");
        var $btn = $("#wknd-import-btn");
        console.log($btn)

    if ($("#native-progress").length === 0) {
        console.log("Creating native progress elements");
        $wrapper.append('<progress id="native-progress" value="0" max="100" style="width:100%; height:25px;"></progress>');
        $wrapper.append('<div id="progress-text" style="text-align:center; font-weight:bold;">0%</div>');
    }

    $wrapper.show();

    var timer = setInterval(function() {
        $.get("/bin/wknd/course-import-status", { parentPath: parentPath }, function(data) {
            console.log("Progress Update:", data);

            $("#native-progress").val(data.percent);
            $("#progress-text").text(data.percent + "% (" + data.processed + " / " + data.total + ")");

            if (data.status === "COMPLETED" || data.percent >= 100) {
                clearInterval(timer);
                ui.clearWait();
                $btn.prop("disabled", false); 
                $btn.removeAttr("disabled");
                ui.alert("Success", "Import completed successfully!", "success");
            } 
            else if (data.status.startsWith("ERROR")) {
                clearInterval(timer);
                ui.clearWait();
                $btn.prop("disabled", false);
                $btn.removeAttr("disabled");
                ui.alert("Error", data.status, "error");
            }
        });
    }, 1000);
}

    $(document).on("click", "#wknd-import-btn", function(e) {
        e.preventDefault();

        var $thisBtn = $(this);
        var csv = $("[name='csvPath']").val();
        var parent = $("[name='parentPath']").val();

        if (!csv || !parent) {
            ui.alert("Selection Missing", "Please select both a CSV file and a destination folder.", "error");
            return;
        }

        $thisBtn.prop("disabled", true);
        $thisBtn.attr("disabled", "disabled");
        ui.wait();

        $.ajax({
            url: "/bin/wknd/import-courses",
            type: "POST",
            data: {
                csvPath: csv,
                parentPath: parent
            },
            success: function(response) {
                ui.clearWait();
                ui.notify("Import Process", "Background task initiated...", "info");
                startProgressMonitoring(parent);
            },
            error: function(xhr) {
                ui.clearWait();
                $thisBtn.prop("disabled", false);
                $thisBtn.removeAttr("disabled");
                var errorMsg = xhr.responseJSON ? xhr.responseJSON.message : "System Error";
                ui.alert("Handoff Failed", "Could not start import: " + errorMsg, "error");
            }
        });
    });
})(document, Granite.$, Granite);