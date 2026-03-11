package com.adobe.aem.guides.wknd.core.models;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;

public class WCMUsePojoSample extends WCMUsePojo {
    private String uppercaseDescription;
    private String title;
    private String description;
    
    @Override
    public void activate() throws Exception {
        String titleProp = getProperties().get("title", String.class);
        this.title = StringUtils.isBlank(titleProp) ? "Default WCMUsePojo title here!" : titleProp;
        this.description = getProperties().get("description", String.class);
        this.uppercaseDescription = StringUtils.isBlank(description) ? "Default WCMUsePojo description here!" : description.toUpperCase();
    }

    public String getUppercaseDescription() {
        return uppercaseDescription;
    }

    public String getTitle() {
        return title;
    }
}
