package com.adobe.aem.guides.wknd.core.models;

import com.adobe.aem.guides.wknd.core.util.CourseItemMapper;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.TagManager;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CourseSliderModel {
    private static final Logger LOG = LoggerFactory.getLogger(CourseSliderModel.class);

    @Inject
    private QueryBuilder queryBuilder;

    @Self
    private SlingHttpServletRequest request;

    @ValueMapValue(name = "courseThumbnail")
    @Optional
    private String courseThumbnail;

    @ValueMapValue
    private String coursePath;

    @ValueMapValue
    private String sortOrder;

    @ValueMapValue
    private boolean includeOngoingCourses;

    @ValueMapValue
    private String[] selectedTags;

    @ValueMapValue
    @Default(longValues = 9)
    private int maxItems;

    private List<CourseItem> courses = new ArrayList<>();

    private static final String COURSES_PATH = "/content/wknd/Courses";
    private static final String COURSE_RESOURCE_TYPE = "wknd/components/structure/course-page";

    @PostConstruct
    protected void init() {
        ResourceResolver resolver = request.getResourceResolver();
        TagManager tagManager = resolver.adaptTo(TagManager.class);
        Map<String, String> predicate = new HashMap<>();
        predicate.put("path", coursePath != null ? coursePath : COURSES_PATH);
        predicate.put("type", "cq:Page");

        predicate.put("1_property", "jcr:content/sling:resourceType");
        predicate.put("1_property.value", COURSE_RESOURCE_TYPE);

        if (!includeOngoingCourses) {
            predicate.put("2_relativedaterange.property", "jcr:content/start-date/date");
            predicate.put("2_relativedaterange.lowerBound", "0");
        }
        if (selectedTags != null && selectedTags.length > 0) {
            predicate.put("3_group.p.or", "true");

            for (int i = 0; i < selectedTags.length; i++) {
                int index = i + 1;
                predicate.put("3_group." + index + "_tagid", selectedTags[i]);
                predicate.put("3_group." + index + "_tagid.property", "jcr:content/course-tags/cq:tags");
            }
        }

        predicate.put("orderby", "@jcr:content/start-date/date");
        predicate.put("orderby.sort", sortOrder != null ? sortOrder : "asc");

        predicate.put("p.limit", String.valueOf(maxItems));
        predicate.put("index", "courseSlider");

        Query query = queryBuilder.createQuery(PredicateGroup.create(predicate),
                resolver.adaptTo(Session.class));
        SearchResult result = query.getResult();

        for (Hit hit : result.getHits()) {
            try {
                CourseItem item = CourseItemMapper.mapResourceToItem(hit.getResource(), tagManager);

                if (item != null) {
                    courses.add(item);
                }
            } catch (Exception e) {
                LOG.error("Error mapping hit to course item", e);
            }
        }
    }

    public List<CourseItem> getCourses() {
        return courses;
    }
}