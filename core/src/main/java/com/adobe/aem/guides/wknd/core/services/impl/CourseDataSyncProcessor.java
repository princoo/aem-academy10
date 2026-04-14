package com.adobe.aem.guides.wknd.core.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.ModificationType;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component(service = SlingPostProcessor.class, immediate = true)
public class CourseDataSyncProcessor implements SlingPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(CourseDataSyncProcessor.class);

    @Override
    public void process(SlingHttpServletRequest request, List<Modification> changes) throws Exception {

        Resource resource = request.getResource();
        ResourceResolver resolver = resource.getResourceResolver();
        String resourceType = resource.getResourceType();
        String nodeName = resource.getName();
        if ("sling:nonexisting".equals(resourceType)) {
            LOG.info("Processing first-time save for resourcetype: {} witht the params: {}", resourceType,
                    request.getParameter("./sling:resourceType"));

            resourceType = request.getParameter("./sling:resourceType");

            if (resourceType == null) {
                resourceType = request.getParameter("sling:resourceType");
            }
        }

        if (resourceType == null) {
            return;
        }
        boolean isTitle = "wknd/components/atomics/title".equals(resourceType) && "hero-course-title".equals(nodeName);
        boolean isImage = "wknd/components/atomics/image".equals(resourceType) && "hero-image".equals(nodeName);

        if (!isTitle && !isImage) {
            LOG.error("not the correct resource type: {}", resourceType);
            return;
        }

        PageManager pageManager = resolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            LOG.error("no page manager found");
            return;
        }

        Page currentPage = pageManager.getContainingPage(resource);

        if (currentPage == null) {
            LOG.error("current page is null");
        }
        if (currentPage != null && currentPage.getTemplate() == null) {
            LOG.error("current page template is null");
        }
        if (currentPage != null && currentPage.getTemplate() != null
                && !currentPage.getTemplate().getPath().contains("course-template")) {
            LOG.error("current page template is not a course page template: {}", currentPage.getTemplate().getPath());
        }
        if (currentPage == null || currentPage.getTemplate() == null ||
                !currentPage.getTemplate().getPath().contains("course-template")) {

            LOG.error("not a course page: {}", currentPage != null ? currentPage.getPath() : "null");

            return;
        }


        Resource jcrContent = currentPage.getContentResource();
        ModifiableValueMap pageProperties = jcrContent.adaptTo(ModifiableValueMap.class);
        ValueMap componentData = resource.getValueMap();

        if (pageProperties == null)

        {
            LOG.error("could not adapt page properties to modifiable value map");
            return;
        }

        for (Modification change : changes) {
            String changedPath = change.getSource();

            if (change.getType() == ModificationType.DELETE) {
                if (isTitle && changedPath.contains("jcr:title")) {
                    pageProperties.remove("courseTitle");
                    LOG.info("Author deleted the Title. Removed courseTitle.");
                } else if (isImage && changedPath.contains("fileReference")) {
                    pageProperties.remove("courseImage");
                    LOG.info("Author deleted the Image. Removed courseImage.");
                }
            }

            else if (change.getType() == ModificationType.CREATE || change.getType() == ModificationType.MODIFY) {
                if (isTitle && changedPath.contains("jcr:title")) {
                    // String newTitle = componentData.get("jcr:title", String.class);
                    String newTitle = request.getParameter("./jcr:title");
                    if (newTitle != null && !newTitle.isEmpty()) {
                        pageProperties.put("courseTitle", newTitle);
                        LOG.info("Successfully synced courseTitle: {}", newTitle);
                    }
                } else if (isImage && changedPath.contains("fileReference")) {
                    // String newImage = componentData.get("fileReference", String.class);
                    String newImage = request.getParameter("./fileReference");
                    // 2. Only save if it's actually there!
                    if (newImage != null && !newImage.isEmpty()) {
                        pageProperties.put("courseImage", newImage);
                        LOG.info("Successfully synced courseImage: {}", newImage);
                    }
                }
            }
        }
    }
}