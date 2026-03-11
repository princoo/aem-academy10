package com.adobe.aem.guides.wknd.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeroSlideItem {

    @ValueMapValue
    private String imagePath;

    @ValueMapValue
    private String pretitle;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String subtitle;

    @ValueMapValue
    private String ctaLink;

    @ValueMapValue
    private String ctaText;

    public String getImagePath() {
        return imagePath;
    }

    public String getPretitle() {
        return pretitle;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getCtaLink() {
        return ctaLink;
    }

    public String getCtaText() {
        return ctaText;
    }
}