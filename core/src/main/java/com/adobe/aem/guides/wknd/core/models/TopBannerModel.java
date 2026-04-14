package com.adobe.aem.guides.wknd.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TopBannerModel {

    @ValueMapValue
    private String editFocus;

    @SlingObject
    private Resource resource;

    private List<SlideItem> sortedSlides = new ArrayList<>();

    @PostConstruct
    protected void init() {
        if (resource != null) {
            ValueMap properties = resource.getValueMap();

            for (int i = 1; i <= 6; i++) {

                String defaultHidden = (i > 2) ? "true" : "false";
                boolean isHidden = Boolean.parseBoolean(properties.get("hideSlide" + i, defaultHidden));

                if (!isHidden) {
                    int order = properties.get("slide" + i + "Order", i);
                    sortedSlides.add(new SlideItem("slide" + i + "_", order));
                }
            }

            Collections.sort(sortedSlides);
        }
    }

    public String getEditFocus() {
        return (editFocus != null) ? editFocus : "0";
    }

    public List<SlideItem> getSortedSlides() {
        return sortedSlides;
    }

    public static class SlideItem implements Comparable<SlideItem> {
        private String namePrefix;
        private int order;

        public SlideItem(String namePrefix, int order) {
            this.namePrefix = namePrefix;
            this.order = order;
        }

        public String getNamePrefix() {
            return namePrefix;
        }

        public int getOrder() {
            return order;
        }

        @Override
        public int compareTo(SlideItem other) {
            return Integer.compare(this.order, other.order);
        }
    }

}
