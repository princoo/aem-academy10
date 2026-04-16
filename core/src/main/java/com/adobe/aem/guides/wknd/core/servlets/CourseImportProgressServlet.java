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
        String path = request.getParameter("parentPath");
        ResourceResolver resolver = request.getResourceResolver();
        Resource resource = resolver.getResource(path + "/jcr:content");

        Map<String, Object> statusMap = new HashMap<>();
        if (resource != null) {
            ValueMap properties = resource.getValueMap();
            long total = properties.get("importTotal", 0L);
            long processed = properties.get("importProcessed", 0L);
            String status = properties.get("importStatus", "IDLE");

            statusMap.put("total", total);
            statusMap.put("processed", processed);
            statusMap.put("status", status);

            int percent = (total > 0) ? (int) ((processed * 100) / total) : 0;
            statusMap.put("percent", percent);
        } else {
            statusMap.put("status", "NOT_FOUND");
        }

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), statusMap);
    }
}