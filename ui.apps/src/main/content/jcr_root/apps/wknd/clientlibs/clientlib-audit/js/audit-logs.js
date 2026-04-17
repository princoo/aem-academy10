(function (document, $, Granite) {
  "use strict";

  var pollInterval = null;
  const POLL_TIME = 15000;

  function fetchAndRender() {
    $.get("/bin/wknd/import-reports-list", function (reports) {
      renderTable(reports);
      managePolling(reports);
    });
  }

  function renderTable(reports) {
    var $tbody = $("#audit-table-body");
    $tbody.empty();

    reports.forEach(function (report) {
      var isRunning = report.status === "RUNNING";

      var row = `
    <tr is="coral-table-row" class="audit-row" data-path="${report.path}">
    <td is="coral-table-cell">${new Date(report.timestamp).toLocaleString()}</td>
    <td is="coral-table-cell">${report.owner}</td>
    <td is="coral-table-cell">${report.total}</td>
        <td is="coral-table-cell">${report.status == "RUNNING" ? "-" : report.duration}</td>
        <td is="coral-table-cell">${report.created}</td>
        <td is="coral-table-cell">${report.updated}</td>
        <td is="coral-table-cell">${report.skipped}</td>
        <td is="coral-table-cell" class="${report.errors > 0 ? "error-cell" : ""}">${report.errors}</td>
        <td is="coral-table-cell">${report.status}</td>
    </tr>
`;
      $tbody.append(row);
    });
  }

  function managePolling(reports) {
    var hasRunningJob = reports.some((r) => r.status === "RUNNING");

    if (hasRunningJob && !pollInterval) {
      console.log("a job is still running");
      pollInterval = setInterval(fetchAndRender, POLL_TIME);
    } else if (!hasRunningJob && pollInterval) {
      console.log("no job is running");
      clearInterval(pollInterval);
      pollInterval = null;
    }
  }

  $(document).on("click", ".audit-row", function () {
    var path = $(this).data("path");
    window.location.href =
      "/apps/wknd/components/tools/audit-detail.html?reportPath=" +
      encodeURIComponent(path);
  });

  $(document).on("click", "#manual-refresh-btn", function () {
    fetchAndRender();
  });

  $(document).ready(function () {
    fetchAndRender();
  });
})(document, Granite.$, Granite);
