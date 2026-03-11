package com.adobe.aem.guides.wknd.core.models;

import com.adobe.aem.guides.wknd.core.services.PageViewCountService;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class)
public class TrackerModel {

    @OSGiService
    private PageViewCountService viewCountService;

    @ScriptVariable
    private Page currentPage;

    @PostConstruct
    protected void init() {
        if (currentPage != null && currentPage.getContentResource() != null) {
            viewCountService.increment(currentPage.getContentResource());
        }
    }
}


