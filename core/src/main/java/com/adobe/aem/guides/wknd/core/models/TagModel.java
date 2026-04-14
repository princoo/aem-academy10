package com.adobe.aem.guides.wknd.core.models;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TagModel {

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue(name = "cq:tags")
    private String[] tagIds;

    private List<String> tagTitles = new ArrayList<>();

    @PostConstruct
    protected void init() {
        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        if (tagManager != null && tagIds != null) {
            for (String id : tagIds) {
                Tag tag = tagManager.resolve(id);
                if (tag != null) {
                    tagTitles.add(tag.getTitle());
                }
            }
        }
    }

    public List<String> getTagTitles() {
        return tagTitles;
    }
}