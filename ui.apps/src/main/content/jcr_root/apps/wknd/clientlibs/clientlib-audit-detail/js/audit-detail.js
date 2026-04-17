(function (document, $, Granite) {
    "use strict";

    var importOffset = 0;
    var pollTimer = null;
    var reportPath = new URLSearchParams(window.location.search).get('reportPath');

    function fetchUpdates() {
        if (!reportPath) return;

        $.get("/bin/wknd/course-import-status", { reportPath: reportPath, offset: importOffset }, function (data) {
            $("#stat-created").text(data.created || 0);
            $("#stat-updated").text(data.updated || 0);
            $("#stat-skipped").text(data.skipped || 0);
            $("#stat-errors").text(data.errors || 0);
            $("#display-status").text(data.status);
            $("#display-path").text(reportPath.split('/').pop());

            // 2. Append Logs
            var $console = $("#wknd-detail-console");
            if (data.newLogs && data.newLogs.length > 0) {
                data.newLogs.forEach(function (logStr) {
                    var entry = JSON.parse(logStr);
                    var color = entry.status === "ERROR" ? "#ff6b68" : 
                                entry.status === "CREATED" ? "#a8c023" : "#4b89dc";
                    
                    $console.append(`<div class="log-line" style="color:${color}">[${entry.status}] ${entry.message}</div>`);
                });

                importOffset = data.newOffset;
            }

            var $statusLine = $("#status-log-line");
            if ($statusLine.length === 0) {
                $statusLine = $('<div id="status-log-line" class="log-line" style="font-weight: bold; margin-top: 10px; border-top: 1px solid #333; padding-top: 10px;"></div>');
                $console.append($statusLine);
            }

            if (data.status === "RUNNING") {
                $statusLine.html('<span class="blink">RUNNING...</span>').css("color", "#4b89dc");
            } else if (data.status === "COMPLETED") {
                $statusLine.text("COMPLETED").css("color", "#a8c023");
            }

            $statusLine.appendTo($console);

            var container = document.getElementById("log-scroll-container"); // auto scroll
            if (container) {
                container.scrollTop = container.scrollHeight;
            }

            if (data.status === "RUNNING") {
                if (!pollTimer) pollTimer = setInterval(fetchUpdates, 1500); // ping
            } else {
                clearInterval(pollTimer);
                pollTimer = null;
            }
        }).fail(function() {
            $("#display-path").text("Error: Report not found.");
        });
    }

    $(document).ready(function () {
        fetchUpdates();
    });

})(document, Granite.$, Granite);