package com.adobe.aem.guides.wknd.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component(service = { Servlet.class })
@SlingServletPaths(value = { "/bin/wknd/import-reports-list" })
public class ImportReportsListServlet extends SlingSafeMethodsServlet {
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        ResourceResolver resolver = request.getResourceResolver();
        Resource reportsRoot = resolver.getResource("/var/wknd/importer/reports");
        List<Map<String, Object>> reports = new ArrayList<>();

        if (reportsRoot != null) {
            for (Resource report : reportsRoot.getChildren()) {
                ValueMap props = report.getValueMap();
                Map<String, Object> map = new HashMap<>();

                long start = props.get("startTime", 0L); // duration
                long end = props.get("endTime", 0L);
                String duration = "--";

                if (start > 0) {
                    long currentTime = (end > 0) ? end : System.currentTimeMillis();
                    long diffSeconds = (currentTime - start) / 1000;
                    duration = (diffSeconds / 60) + "m " + (diffSeconds % 60) + "s";
                }

                map.put("path", report.getPath());
                map.put("name", report.getName());
                map.put("status", props.get("status", "UNKNOWN"));
                map.put("percent", props.get("percent", 0));
                map.put("processed", props.get("processed", 0L));
                map.put("total", props.get("total", 0L));

                map.put("duration", duration);
                map.put("created", props.get("createdCount", 0));
                map.put("updated", props.get("updatedCount", 0));
                map.put("skipped", props.get("skippedCount", 0));
                map.put("errors", props.get("errorCount", 0));
                map.put("owner", props.get("owner", "unknown"));

                try {
                    String timestampStr = report.getName().replace("report_", "");
                    map.put("timestamp", Long.parseLong(timestampStr));
                } catch (Exception e) {
                    map.put("timestamp", 0L);
                }

                reports.add(map);
            }
        }

        reports.sort((a, b) -> ((Long) b.get("timestamp")).compareTo((Long) a.get("timestamp"))); // am sort to get the
                                                                                                  // pending ones on top

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), reports);
    }

}
