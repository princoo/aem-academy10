package com.adobe.aem.guides.wknd.core.models.footer;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LinkListModel {

    @ValueMapValue
    private String title;

    @ChildResource(name = "links")
    private List<FooterLinkItem> linksResource;

    public String getTitle() {
        return title;
    }

    public List<FooterLinkItem> getLinks() {
        return linksResource;
    }
}