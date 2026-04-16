package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.models.CourseModel;
import com.adobe.aem.guides.wknd.core.services.CourseImporterService;
import com.adobe.aem.guides.wknd.core.services.ImportLogEntry;
import com.adobe.aem.guides.wknd.core.util.JcrUtils;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
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
        if (myThreadPool == null) {
            return "Error: Thread pool 'wknd-importer-pool' not ready. Check Web Console.";
        }

        myThreadPool.execute(() -> {
            processCourseImportTask(csvPath, parentPath);
        });
        return "Import process started in the background. Monitoring progress via JCR.";
    }

    private void processCourseImportTask(String csvPath, String parentPath) {
        List<ImportLogEntry> executionLogs = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;
        int skippedCount = 0;
        int processedCount = 0;

        Map<String, Object> authInfo = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SUBSERVICE_NAME);

        try (ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {

            InputStream is = getAssetInputStream(resolver, csvPath);
            if (is == null) {
                logger.error("COURSE IMPORTER: CSV file not found at {}", csvPath);
                updateStatus(resolver, parentPath, "ERROR: CSV NOT FOUND");
                return;
            }

            List<String> lines = IOUtils.readLines(is, StandardCharsets.UTF_8);
            int totalRows = Math.max(0, lines.size() - 1);

            updateProgress(resolver, parentPath, totalRows, 0, "RUNNING");
            resolver.commit();

            boolean isHeader = true;
            for (String line : lines) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                CourseModel course = parseCsvLine(line);
                try {
                    if (course == null) {
                        logger.error("COURSE IMPORTER: Skipped line due to incomplete properties: {}", line);
                        skippedCount++;
                        continue;
                    }

                    String pagePath = parentPath + "/" + course.getId();
                    boolean isNewPage = (resolver.getResource(pagePath) == null);
                    Page coursePage = getOrCreatePage(resolver, parentPath, course);

                    boolean propertiesChanged = updateCourseContent(resolver, coursePage, course);

                    if (isNewPage || propertiesChanged) {
                        resolver.commit();
                        successCount++;
                    } else {
                        skippedCount++;
                        logger.info("COURSE IMPORTER: No changes for {}, skipping write.",
                                course.getId());
                    }
                    executionLogs
                            .add(new ImportLogEntry(course.getId(), "CREATED", "Page created successfully", pagePath));
                    Thread.sleep(500); // using this for testing the progress bar
                } catch (Exception e) {
                    logger.error("COURSE IMPORTER: Failed to process line: {}", line, e);
                    executionLogs.add(new ImportLogEntry(course.getId(), "ERROR", e.getMessage(), parentPath));
                    errorCount++;
                } finally {
                    processedCount++;
                    updateProgress(resolver, parentPath, totalRows, processedCount, "RUNNING");
                    resolver.commit();
                }
            }

            updateProgress(resolver, parentPath, totalRows, processedCount, "COMPLETED");
            resolver.commit();
            logger.info("COURSE IMPORTER: Background task finished. Success: {} | Skipped: {} | Errors: {}",
                    successCount, skippedCount,
                    errorCount);

        } catch (Exception e) {
            logger.error("COURSE IMPORTER: Critical error in background thread", e);
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
    private boolean updateCourseContent(ResourceResolver resolver, Page page, CourseModel course) {
        Resource contentResource = page.getContentResource();
        if (contentResource == null)
            return false;

        boolean changed = false;

        changed |= JcrUtils.setAtomicProperty(resolver, contentResource, "hero-image", "fileReference",
                course.getThumbnail(), "wknd/components/atomics/image");

        changed |= JcrUtils.setAtomicProperty(resolver, contentResource, "hero-course-title", "jcr:title",
                course.getTitle(), "wknd/components/atomics/title");

        changed |= JcrUtils.setAtomicProperty(resolver, contentResource, "hero-course-description", "text",
                course.getDescription(), "wknd/components/atomics/text");

        changed |= JcrUtils.setAtomicProperty(resolver, contentResource, "course-tags", "cq:tags",
                course.getTags(), "wknd/components/content/tag-picker");

        changed |= JcrUtils.setAtomicProperty(resolver, contentResource, "start-date", "date",
                course.getStartDate(), "wknd/components/content/date-picker");

        return changed;
    }

    // write progress in jcr:content
    private void updateProgress(ResourceResolver resolver, String path, int total, int processed, String status) {
        try {
            Resource parent = resolver.getResource(path);
            if (parent == null)
                return;

            Resource jcrContent = getOrCreateJcrContent(resolver, parent);

            ModifiableValueMap mvm = jcrContent.adaptTo(ModifiableValueMap.class);
            if (mvm != null) {
                mvm.put("importTotal", (long) total);
                mvm.put("importProcessed", (long) processed);
                mvm.put("importStatus", status);
                resolver.commit();
            }
        } catch (PersistenceException e) {
            logger.error("Error saving progress to JCR", e);
        }
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
}