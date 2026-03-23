package com.adobe.aem.guides.wknd.core.models;

import java.util.List;

import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import com.adobe.aem.guides.wknd.core.models.header.HeaderLinkItem;

public class EditorialLinksModel {
    @ChildResource
    private List<HeaderLinkItem> links;

    public List<HeaderLinkItem> getLinks() {
        return links;
    }
}
