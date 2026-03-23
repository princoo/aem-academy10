package com.adobe.aem.guides.wknd.core.models.footer;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterColumn {
    @ValueMapValue
    private String columnTitle;

    @ChildResource(name = "editorialLinks")
    private List<FooterLinkItem> links;
    

    public String getColumnTitle() { return columnTitle; }
    public List<FooterLinkItem> getLinks() { return links; }
}