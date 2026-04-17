package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.models.CourseModel;
import com.adobe.aem.guides.wknd.core.services.CourseImporterService;
import com.adobe.aem.guides.wknd.core.services.ImportLogEntry;
import com.adobe.aem.guides.wknd.core.services.PropertyUpdateResult;
import com.adobe.aem.guides.wknd.core.util.JcrUtils;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.commons.threads.ThreadPool;
import org.apache.sling.commons.threads.ThreadPoolManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = CourseImporterService.class, immediate = true)
public class CourseImporterServiceImpl implements CourseImporterService {

    private static final Logger logger = LoggerFactory.getLogger(CourseImporterServiceImpl.class);

    private static final String SUBSERVICE_NAME = "wknd-backend-service";
    private static final String TEMPLATE_PATH = "/apps/wknd/templates/course-template";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    @Reference
    private ThreadPoolManager threadPoolManager;

    @Activate
    protected void activate() {
        myThreadPool = threadPoolManager.get("wknd-importer-pool");
    }

    @Deactivate
    protected void deactivate() {
        if (myThreadPool != null)
            threadPoolManager.release(myThreadPool);
    }

    private ThreadPool myThreadPool;

    @Override
    public String importCourses(String csvPath, String parentPath) {
        long startTime = System.currentTimeMillis();
        String REPORT_PATH = "/var/wknd/importer/reports/report_" + startTime;
        if (myThreadPool == null) {
            return "Error: Thread pool 'wknd-importer-pool' not ready. Check Web Console.";
        }

        myThreadPool.execute(() -> {
            Map<String, Object> authInfo = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE,
                    SUBSERVICE_NAME);
            try (ResourceResolver serviceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {
                processCourseImportTask(serviceResolver, csvPath, parentPath, REPORT_PATH, startTime,"SYSTEM_SCHEDULER");

            } catch (LoginException e) {
                logger.error("Background task failed to login", e);
            }
        });
        return REPORT_PATH;
    }

    @Override
    public void processCourseImportTask(ResourceResolver resolver, String csvPath, String parentPath, String reportPath, long startTime, String initiatingUser) {
        List<ImportLogEntry> executionLogs = new ArrayList<>();
        int successCount = 0;
        int createdCount = 0;
        int updatedCount = 0;
        int errorCount = 0;
        int skippedCount = 0;
        int processedCount = 0;
        String status;
        String detail;
        ImportLogEntry currentEntry = null;
        
        try{

            InputStream is = getAssetInputStream(resolver, csvPath);
            if (is == null) {
                logger.error("COURSE IMPORTER: CSV file not found at {}", csvPath);
                updateStatus(resolver, parentPath, "ERROR: CSV NOT FOUND");
                return;
            }

            List<String> lines = IOUtils.readLines(is, StandardCharsets.UTF_8);
            int totalRows = Math.max(0, lines.size() - 1);

            Resource reportResource = ResourceUtil.getOrCreateResource(resolver, reportPath,
                    Collections.singletonMap("jcr:primaryType", "nt:unstructured"), "sling:Folder", true);

            ModifiableValueMap reportMvm = reportResource.adaptTo(ModifiableValueMap.class);
            reportMvm.put("startTime", startTime);
            reportMvm.put("status", "RUNNING");
            reportMvm.put("total", totalRows);
            reportMvm.put("processed", 0L);
            reportMvm.put("percent", 0);
            reportMvm.put("owner", initiatingUser);
            reportMvm.remove("logData");
            resolver.commit();

            try {

                boolean isHeader = true;
                for (String line : lines) {
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }

                    CourseModel course = parseCsvLine(line);
                    if (course == null) {
                        logger.error("COURSE IMPORTER: Skipped line due to incomplete properties: {}", line);
                        skippedCount++;
                        continue;
                    }
                    String courseId = course.getId();

                    try {
                        String pagePath = parentPath + "/" + courseId;
                        boolean isNewPage = (resolver.getResource(pagePath) == null);
                        Page coursePage = getOrCreatePage(resolver, parentPath, course);
                        PropertyUpdateResult updateResult = updateCourseContent(resolver, coursePage, course);

                        if (isNewPage && updateResult.isChanged()) {
                            status = "CREATED";
                            detail = courseId + " - course created.";
                            createdCount++;
                            resolver.commit();
                        } else if (updateResult.isChanged() && !isNewPage) {
                            status = "UPDATED";
                            detail = courseId + " - existing page found; properties updated: "
                                    + String.join(", ", updateResult.getUpdatedProperties());
                            updatedCount++;
                            resolver.commit();
                        } else {
                            status = "SKIPPED";
                            detail = courseId + " - content matches; no update made.";
                            skippedCount++;
                            logger.info("COURSE IMPORTER: No changes for {}, skipping write.", courseId);
                        }
                        currentEntry = new ImportLogEntry(courseId, status, detail, pagePath);
                        appendLogToVault(reportMvm, currentEntry);

                        Thread.sleep(2000);
                    } catch (Exception e) {
                        logger.error("COURSE IMPORTER: Failed to process line: {}", line, e);
                        executionLogs.add(new ImportLogEntry(courseId, "ERROR", e.getMessage(), parentPath));
                        errorCount++;
                    } finally {
                        processedCount++;
                        reportMvm.put("processed", processedCount);
                        reportMvm.put("percent", (int) (((double) processedCount / totalRows) * 100));

                        reportMvm.put("createdCount", createdCount);
                        reportMvm.put("updatedCount", updatedCount);
                        reportMvm.put("skippedCount", skippedCount);
                        reportMvm.put("errorCount", errorCount);
                        if (processedCount % 5 == 0) {
                            resolver.commit();
                        }

                    }
                }

                resolver.commit();
                logger.info(
                        "COURSE IMPORTER: Backgrounddd task finished. Created: {} | Updated: {} | Skipped: {} | Errors: {}",
                        createdCount, updatedCount, skippedCount, errorCount);
            } catch (Exception e) {
                logger.error("COURSE IMPORTER: Critical error in background thread", e);
            } finally {
                long endTime = System.currentTimeMillis();
                reportMvm.put("endTime", endTime);
                reportMvm.put("status", "COMPLETED");
                reportMvm.put("percent", 100);
                resolver.commit();
            }

        } catch (Exception e) {
            logger.error("COURSE IMPORTER: Critical error in background thread", e);
        } finally{
            logger.info("Executing import logic using user: {}", resolver.getUserID());
        }
    }

    // extract the inputstream from a dam
    private InputStream getAssetInputStream(ResourceResolver resolver, String path) {
        Resource res = resolver.getResource(path);
        if (res != null) {
            Asset asset = res.adaptTo(Asset.class);
            if (asset != null) {
                Rendition rendition = asset.getOriginal();
                return (rendition != null) ? rendition.adaptTo(InputStream.class) : null;
            }
        }
        return null;
    }

    // parse csv line into an object
    private CourseModel parseCsvLine(String line) {
        String[] columns = line.split(",", -1);
        if (columns.length < 6)
            return null;

        String id = columns[0].trim();
        String title = columns[1].trim();
        String desc = columns[2].trim();
        String date = columns[3].trim();

        String rawTags = columns[4].trim().replace("\"", "");
        String[] tags = rawTags.isEmpty() ? new String[0] : rawTags.split(",");

        String thumb = columns[5].trim();

        return new CourseModel(id, title, desc, date, thumb, tags);
    }

    // handle page manager logic
    private Page getOrCreatePage(ResourceResolver resolver, String parentPath, CourseModel course) throws WCMException {
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        String pagePath = parentPath + "/" + course.getId();
        Page page = pageManager.getPage(pagePath);

        if (page == null) {
            logger.info("COURSE IMPORTER: Creating new page for {}", course.getId());
            page = pageManager.create(parentPath, course.getId(), TEMPLATE_PATH, course.getTitle());
        }
        return page;
    }

    // update of atomic components
    private PropertyUpdateResult updateCourseContent(ResourceResolver resolver, Page page, CourseModel course) {
        Resource contentResource = page.getContentResource();
        List<String> changedNames = new ArrayList<>();
        if (contentResource == null)
            return new PropertyUpdateResult(false, changedNames);

        if (JcrUtils.setAtomicProperty(resolver, contentResource, "hero-image", "fileReference",
                course.getThumbnail().trim(), "wknd/components/atomics/image")) {
            changedNames.add("Thumbnail Image");
        }

        if (JcrUtils.setAtomicProperty(resolver, contentResource, "hero-course-title", "jcr:title",
                course.getTitle(), "wknd/components/atomics/title")) {
            changedNames.add("Course Title");
        }

        if (JcrUtils.setAtomicProperty(resolver, contentResource, "hero-course-description", "text",
                course.getDescription(), "wknd/components/atomics/text")) {
            changedNames.add("Description");
        }

        if (JcrUtils.setAtomicProperty(resolver, contentResource, "course-tags", "cq:tags",
                course.getTags(), "wknd/components/content/tag-picker")) {
            changedNames.add("Tags");
        }

        if (JcrUtils.setAtomicProperty(resolver, contentResource, "start-date", "date",
                course.getStartDate(), "wknd/components/content/date-picker")) {
            changedNames.add("Start Date");
        }

        return new PropertyUpdateResult(!changedNames.isEmpty(), changedNames);
    }

    private void updateStatus(ResourceResolver resolver, String path, String status) {
        Resource parent = resolver.getResource(path);
        if (parent != null) {
            try {
                Resource jcrContent = getOrCreateJcrContent(resolver, parent);
                ModifiableValueMap mvm = jcrContent.adaptTo(ModifiableValueMap.class);

                if (mvm != null) {
                    mvm.put("importStatus", status);
                    resolver.commit();
                }
            } catch (Exception e) {
                logger.error("COURSE IMPORTER: Failed to update status for {}", path, e);
            }
        }
    }

    private Resource getOrCreateJcrContent(ResourceResolver resolver, Resource parent) throws PersistenceException {
        Resource jcrContent = parent.getChild("jcr:content");
        if (jcrContent == null) {
            Map<String, Object> props = new HashMap<>();
            props.put("jcr:primaryType", "nt:unstructured");
            jcrContent = resolver.create(parent, "jcr:content", props);
        }
        return jcrContent;
    }

    private void appendLogToVault(ModifiableValueMap mvm, ImportLogEntry entry) {
        String[] currentLogs = mvm.get("logData", new String[0]);
        String[] newLogs = Arrays.copyOf(currentLogs, currentLogs.length + 1);
        newLogs[newLogs.length - 1] = new Gson().toJson(entry);
        mvm.put("logData", newLogs);
    }

}