package com.adobe.aem.guides.wknd.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.guides.wknd.core.services.CourseImporterService;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(service = Servlet.class)
@SlingServletPaths(value = "/bin/wknd/import-courses")
public class CourseImporterServlet extends SlingAllMethodsServlet {
    private static final Logger logger = LoggerFactory.getLogger(CourseImporterServlet.class);

    @Reference
    private transient CourseImporterService importerService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        logger.info("COURSE IMPORTER SERVICE: Starting on-demand import...");
        String csvPath = request.getParameter("csvPath");
        String parentPath = request.getParameter("parentPath");

        if (csvPath == null || parentPath == null) {
            response.setStatus(400);
            response.getWriter().write("Missing required parameters: csvPath or parentPath.");
            return;
        }
        try {
            String message = importerService.importCourses(csvPath, parentPath);
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\": \"" + message + "\"}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Import failed: " + e.getMessage());
        }
    }
}