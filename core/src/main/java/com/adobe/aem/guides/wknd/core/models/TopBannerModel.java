package com.adobe.aem.guides.wknd.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TopBannerModel {
    @ValueMapValue
    private int sliderCount;

    public List<Integer> getSlideIndexes() {
        List<Integer> indexes = new ArrayList<>();

        int totalSlides = (sliderCount > 0) ? sliderCount : 1;

        for (int i = 0; i < totalSlides; i++) {
            indexes.add(i);
        }

        return indexes;
    }

}
