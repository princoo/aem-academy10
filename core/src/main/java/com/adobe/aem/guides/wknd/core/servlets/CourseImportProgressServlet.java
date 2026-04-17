package com.adobe.aem.guides.wknd.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(service = { Servlet.class })
@SlingServletPaths(value = { "/bin/wknd/course-import-status" })
public class CourseImportProgressServlet extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String reportPath = request.getParameter("reportPath");
        String offsetStr = request.getParameter("offset");
        int offset = (offsetStr != null) ? Integer.parseInt(offsetStr) : 0;

        ResourceResolver resolver = request.getResourceResolver();
        Map<String, Object> statusMap = new HashMap<>();

        if (reportPath != null) {
            Resource reportResource = resolver.getResource(reportPath);

            if (reportResource != null) {
                ValueMap props = reportResource.getValueMap();

                statusMap.put("total", props.get("total", 0L));
                statusMap.put("processed", props.get("processed", 0L));
                statusMap.put("percent", props.get("percent", 0));
                statusMap.put("created", props.get("createdCount", 0L));
                statusMap.put("updated", props.get("updatedCount", 0L));
                statusMap.put("skipped", props.get("skippedCount", 0L));
                statusMap.put("error", props.get("errorCount", 0L));
                statusMap.put("status", props.get("status", "RUNNING"));

                String[] allLogs = props.get("logData", new String[0]);
                java.util.List<String> newLogs = new java.util.ArrayList<>();
                if (allLogs.length > offset) {
                    for (int i = offset; i < allLogs.length; i++) {
                        newLogs.add(allLogs[i]);
                    }
                }
                statusMap.put("newLogs", newLogs);
                statusMap.put("newOffset", allLogs.length);

            } else {
                statusMap.put("status", "REPORT_NOT_READY");
            }
        } else {
            statusMap.put("status", "MISSING_PATH");
        }

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), statusMap);
    }
}