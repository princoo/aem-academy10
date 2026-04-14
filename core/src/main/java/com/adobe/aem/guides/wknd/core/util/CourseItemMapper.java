package com.adobe.aem.guides.wknd.core.util;

import com.adobe.aem.guides.wknd.core.models.CourseItem;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.sling.api.resource.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CourseItemMapper {

    public static CourseItem mapResourceToItem(Resource pageResource, TagManager tagManager) {
        Resource jcrContent = pageResource.getChild("jcr:content");
        if (jcrContent == null)
            return null;

        String link = pageResource.getPath() + ".html";
        String image = jcrContent.getValueMap().get("courseThumbnail", String.class);
        if (image == null || image.isEmpty()) {
            Resource imageRes = jcrContent.getChild("hero-image");
            image = (imageRes != null)
                    ? imageRes.getValueMap().get("fileReference", "/content/dam/wknd/placeholder.jpg")
                    : "/content/dam/wknd/placeholder.jpg";
        }

        Resource titleRes = jcrContent.getChild("hero-course-title");
        String title = (titleRes != null) ? titleRes.getValueMap().get("jcr:title", "Untitled") : "Untitled";

        Resource descRes = jcrContent.getChild("hero-course-description");
        String description = (descRes != null) ? descRes.getValueMap().get("text", "No description") : "No description";

        Resource dateRes = jcrContent.getChild("start-date");
        Calendar rawDate = (dateRes != null) ? dateRes.getValueMap().get("date", Calendar.class) : null;
        String formattedDate = DateUtils.formatCourseDate(rawDate);

        List<String> tagTitles = new ArrayList<>();
        Resource tagsRes = jcrContent.getChild("course-tags");
        if (tagsRes != null && tagManager != null) {
            String[] tagIds = tagsRes.getValueMap().get("cq:tags", new String[0]);
            for (String id : tagIds) {
                Tag tag = tagManager.resolve(id);
                if (tag != null)
                    tagTitles.add(tag.getTitle());
            }
        }

        return new CourseItem(title, image, description, link, formattedDate, rawDate, tagTitles);
    }
}