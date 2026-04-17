(function (document, $, Granite) {
    "use strict";

    var ui = $(window).adaptTo("foundation-ui");

    function startProgressMonitoring(parentPath, reportPath) {
        var $wrapper = $("#wknd-progress-wrapper");
        var $btn = $("#wknd-import-btn");

        $wrapper.empty().show();
        $wrapper.append(
            '<div style="margin-bottom: 15px; padding: 15px; border: 1px dashed #4b89dc; background: #f0f7ff; text-align: center; border-radius: 4px;">' +
                '<p style="margin: 0 0 10px 0; font-size: 14px; color: #333;">Import task is running in the background.</p>' +
                '<a href="/apps/wknd/components/tools/audit-detail.html?reportPath=' + encodeURIComponent(reportPath) + '" ' +
                   'target="_blank" class="coral-Link" style="font-weight: bold; font-size: 15px; text-decoration: none; display: inline-flex; align-items: center; gap: 8px;">' +
                    '<coral-icon icon="terminal" size="XS"></coral-icon> Open Audit Log' +
                '</a>' +
            '</div>',
            '<progress id="native-progress" value="0" max="100" style="width:100%; height:20px;"></progress>',
            '<div id="progress-text" style="text-align:center; font-weight:bold; margin-top: 10px;">0%</div>'
        );

        var timer = setInterval(function () {
            $.get("/bin/wknd/course-import-status", { reportPath: reportPath, offset: 0 }, function (data) {
                $("#native-progress").val(data.percent);
                $("#progress-text").text(data.percent + "% (" + data.processed + " / " + data.total + ")");

                if (data.status === "COMPLETED") {
                    clearInterval(timer);
                    ui.clearWait();
                    $btn.prop("disabled", false).removeAttr("disabled");
                    ui.alert("Success", "Import finished. Check the log for details.", "success");
                } else if (data.status.startsWith("ERROR")) {
                    clearInterval(timer);
                    ui.clearWait();
                    $btn.prop("disabled", false).removeAttr("disabled");
                    ui.alert("Error", data.status, "error");
                }
            });
        }, 1500);
    }

    $(document).on("click", "#wknd-import-btn", function (e) {
        e.preventDefault();
        var csv = $("[name='csvPath']").val();
        var parent = $("[name='parentPath']").val();

        if (!csv || !parent) {
            ui.alert("Missing Input", "Please select a CSV and a destination folder.", "error");
            return;
        }

        $(this).prop("disabled", true);
        ui.wait();

        $.ajax({
            url: "/bin/wknd/import-courses",
            type: "POST",
            data: { csvPath: csv, parentPath: parent },
            success: function (response) {
                ui.clearWait();
                startProgressMonitoring(parent, response.reportPath); // monitor progress
            },
            error: function () {
                ui.clearWait();
                $("#wknd-import-btn").prop("disabled", false);
                ui.alert("System Error", "Could not initiate import.", "error");
            }
        });
    });
})(document, Granite.$, Granite);