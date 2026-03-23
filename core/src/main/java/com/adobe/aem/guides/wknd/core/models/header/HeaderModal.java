package com.adobe.aem.guides.wknd.core.models.header;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderModal {

    @ValueMapValue
    private String navRoot;
    @ChildResource(name = "editorialLinks")
    private List<HeaderLinkItem> editorialLinks;

    @SlingObject
    private Resource currentResource;
    @SlingObject
    private ResourceResolver resourceResolver;

    private List<Page> childPages;

    @PostConstruct
    protected void init() {
        childPages = new ArrayList<>();
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        if (pageManager != null) {
            Page rootPage = null;

            if (navRoot != null && !navRoot.isEmpty()) {
                rootPage = pageManager.getPage(navRoot);
            }

            if (rootPage == null) {
                rootPage = pageManager.getContainingPage(currentResource);
            }

            if (rootPage != null) {
                Iterator<Page> children = rootPage.listChildren();

                while (children.hasNext()) {
                    childPages.add(children.next());
                }
            }
        }
    }

    public List<HeaderLinkItem> getEditorialLinks() {
        return editorialLinks;
    }

    public List<Page> getChildPages() {
        return childPages;
    }
}
