package com.adobe.aem.guides.wknd.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
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
public class CourseImporterServlet extends SlingSafeMethodsServlet {
    private static final Logger logger = LoggerFactory.getLogger(CourseImporterServlet.class);

    @Reference
    private transient CourseImporterService importerService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        logger.info("COURSE IMPORTER SERVICE: Starting on-demand import...");
        response.setContentType("text/plain");
        String csvPath = request.getParameter("csvPath");
        String parentPath = request.getParameter("parentPath");

        try {
            String result = importerService.importCourses(csvPath, parentPath);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Triggered On-Demand: " + result);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Import failed: " + e.getMessage());
        }
    }
}