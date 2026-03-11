package com.adobe.aem.guides.wknd.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AcademyLearningDemo {
    @Inject
    @Named("jcr:title")
    private String title;

    @Inject
    @Optional
    private String customDescription;

    @PostConstruct
    protected void init() {
        this.customDescription = StringUtils.isNotBlank(customDescription) ? customDescription.toUpperCase()
                : "Not Available";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCustomDescription() {
        return customDescription;
    }

    public void setCustomDescription(String customDescription) {
        this.customDescription = customDescription;
    }
}
