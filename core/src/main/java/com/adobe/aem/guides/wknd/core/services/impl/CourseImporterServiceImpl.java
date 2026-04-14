package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.services.CourseImporterService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component(service = CourseImporterService.class, immediate = true)
public class CourseImporterServiceImpl implements CourseImporterService {

    private static final Logger logger = LoggerFactory.getLogger(CourseImporterServiceImpl.class);

    // Update these paths if they differ in your exact setup
    private static final String SUBSERVICE_NAME = "wknd-backend-service";
    private static final String TEMPLATE_PATH = "/apps/wknd/templates/course-template";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public String importCourses(String csvPath, String parentPath) {
        int successCount = 0;
        int errorCount = 0;

        Map<String, Object> authInfo = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SUBSERVICE_NAME);

        try (ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {

            Resource fileResource = resolver.getResource(csvPath);
            if (fileResource != null) {
                Asset asset = fileResource.adaptTo(Asset.class);
                if (asset != null) {
                    Rendition rendition = asset.getRendition("original");
                    InputStream is = rendition.adaptTo(InputStream.class);

                    if (is != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                        String line;
                        boolean isHeader = true;
                        PageManager pageManager = resolver.adaptTo(PageManager.class);

                        // reading our lines
                        while ((line = reader.readLine()) != null) {
                            if (isHeader) {
                                isHeader = false;
                                continue;
                            }

                            String[] columns = line.split(",", -1);
                            if (columns.length >= 6) {
                                String courseId = columns[0].trim();
                                String courseTitle = columns[1].trim();
                                String courseDescription = columns[2].trim();
                                String startDate = columns[3].trim();

                                String rawTags = columns[4].trim().replace("\"", "");
                                String[] courseTags = rawTags.isEmpty() ? new String[0] : rawTags.split(",");
                                for (int i = 0; i < courseTags.length; i++) {
                                    courseTags[i] = courseTags[i].trim();
                                }

                                String courseThumbnail = columns[5].trim();
                                String pagePath = parentPath + "/" + courseId;

                                // 3. Create or Update the Page
                                try {
                                    Page coursePage = pageManager.getPage(pagePath);
                                    if (coursePage == null) {
                                        logger.info("COURSE IMPORTER: Creating new page at {}", pagePath);
                                        coursePage = pageManager.create(parentPath, courseId, TEMPLATE_PATH,
                                                courseTitle);
                                    } else {
                                        logger.info("COURSE IMPORTER: Page already exists. Updating properties for {}",
                                                courseId);
                                    }

                                    // access the jcr content to add the properties
                                    Resource contentResource = coursePage.getContentResource();
                                    updateComponentProperty(resolver, contentResource, "hero-image", "fileReference",
                                            courseThumbnail, "wknd/components/atomics/image");
                                    updateComponentProperty(resolver, contentResource, "hero-course-title", "jcr:title",
                                            courseTitle, "wknd/components/atomics/title");
                                    updateComponentProperty(resolver, contentResource, "hero-course-description",
                                            "text",
                                            courseDescription, "wknd/components/atomics/text");
                                    updateComponentProperty(resolver, contentResource, "course-tags", "cq:tags",
                                            courseTags, "wknd/components/content/tag-picker");
                                    updateComponentProperty(resolver, contentResource, "start-date", "date",
                                            startDate, "wknd/components/content/date-picker ");

                                    resolver.commit();
                                    successCount++;

                                } catch (WCMException | PersistenceException e) {
                                    logger.error("COURSE IMPORTER: Failed to create/update page for {}", courseId, e);
                                    errorCount++;
                                }
                            }
                        }
                    }
                }
            } else {
                return "Error: Could not find CSV at " + csvPath;
            }

        } catch (Exception e) {
            logger.error("Error getting Service Resource Resolver", e);
            return "Error: Could not get Service Resource Resolver";
        }

        return String.format("Import Finished! Successfully processed %d courses. Errors: %d.", successCount,
                errorCount);
    }

    // helper for updting the atomic components
    private void updateComponentProperty(ResourceResolver resolver, Resource parent, String nodeName, String propName,
            Object value, String resourceType) {
        try {
            Resource child = parent.getChild(nodeName);

            if (child == null) {
                Map<String, Object> props = new HashMap<>();
                props.put("jcr:primaryType", "nt:unstructured");
                props.put("sling:resourceType", resourceType);
                child = resolver.create(parent, nodeName, props);

                logger.info("COURSE IMPORTER: Created missing component node: {}", nodeName);
            }

            if (child != null) {
                ModifiableValueMap mvm = child.adaptTo(ModifiableValueMap.class);
                if (mvm != null && value != null) {
                    mvm.put(propName, value);
                }
            }
        } catch (PersistenceException e) {
            logger.error("Error creating component node {}", nodeName, e);
        }
    }
}