package com.adobe.aem.guides.wknd.core.models;

import java.util.List;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.api.resource.Resource;
import com.adobe.aem.guides.wknd.core.models.header.HeaderLinkItem;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class EditorialLinksModel {
    @ChildResource
    private List<HeaderLinkItem> links;

    public List<HeaderLinkItem> getLinks() {
        return links;
    }
}
