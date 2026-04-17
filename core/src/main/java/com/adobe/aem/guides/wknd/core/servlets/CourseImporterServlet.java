package com.adobe.aem.guides.wknd.core.servlets;

import com.adobe.aem.guides.wknd.core.services.CourseImporterService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.apache.sling.commons.threads.ThreadPool;
import org.apache.sling.commons.threads.ThreadPoolManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Component(service = Servlet.class)
@SlingServletPaths(value = "/bin/wknd/import-courses")
public class CourseImporterServlet extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(CourseImporterServlet.class);
    private static final String SUBSERVICE_NAME = "wknd-backend-service";

    @Reference
    private CourseImporterService importerService;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ThreadPoolManager threadPoolManager;

    private ThreadPool myThreadPool;

    @Activate
    protected void activate() {
        myThreadPool = threadPoolManager.get("wknd-importer-pool");
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String initiatingUser = request.getResourceResolver().getUserID();
        
        String csvPath = request.getParameter("csvPath");
        String parentPath = request.getParameter("parentPath");
        
        if (csvPath == null || parentPath == null) {
            response.setStatus(400);
            response.getWriter().write("{\"error\": \"Missing parameters\"}");
            return;
        }

        long startTime = System.currentTimeMillis();
        String reportPath = "/var/wknd/importer/reports/report_" + startTime;

        if (myThreadPool != null) {
            myThreadPool.execute(() -> {
                Map<String, Object> authInfo = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SUBSERVICE_NAME);
                try (ResourceResolver threadResolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {
                    
                    importerService.processCourseImportTask(threadResolver, csvPath, parentPath, reportPath, startTime, initiatingUser);
                    
                } catch (LoginException e) {
                    log.error("COURSE IMPORTER: Background login failed", e);
                }
            });
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"reportPath\": \"" + reportPath + "\"}");
    }
}