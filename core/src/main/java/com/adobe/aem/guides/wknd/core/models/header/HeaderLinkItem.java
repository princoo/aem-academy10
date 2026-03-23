package com.adobe.aem.guides.wknd.core.models.header;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderLinkItem {

    @ValueMapValue
    private String linkText;

    @ValueMapValue
    private String linkUrl;

    @ValueMapValue
    private String iconClass;

    public String getLinkText() {
        return linkText;
    }

    public String getLinkUrl() {
        if (linkUrl != null && linkUrl.startsWith("/content")) {
            return linkUrl + ".html";
        }
        return linkUrl;
    }

    public String getIconClass() {
        return iconClass;
    }
}