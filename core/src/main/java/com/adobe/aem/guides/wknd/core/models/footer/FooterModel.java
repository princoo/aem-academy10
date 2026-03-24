package com.adobe.aem.guides.wknd.core.models.footer;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterModel {


    @ValueMapValue
    private String copyrightText;

    @ChildResource
    private List<FooterLinkItem> utilsLinks;

    @ValueMapValue
    private String columnCount;

    private List<Integer> columnsToRender;

    @PostConstruct
    protected void init() {
        columnsToRender = new ArrayList<>();

        int count = (columnCount != null) ? Integer.parseInt(columnCount) : 4;

        for (int i = 1; i <= count; i++) {
            columnsToRender.add(i);
        }
    }

    public String getCopyrightText() {
        return copyrightText;
    }

    public List<FooterLinkItem> getUtilsLinks() {
        return utilsLinks;
    }

    public List<Integer> getColumnsToRender() {
        return columnsToRender;
    }

    public String getColumnCount() {
        return columnCount;
    }
}