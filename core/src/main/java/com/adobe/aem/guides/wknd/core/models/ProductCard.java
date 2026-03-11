package com.adobe.aem.guides.wknd.core.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

// import com.adobe.aem.guides.wknd.core.services.WeatherServiceImpl;
import com.day.cq.wcm.api.Page;

import javax.annotation.PostConstruct;

@Model(adaptables = { Resource.class,
        SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductCard {
    private String searchQuery;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String description;

    @ValueMapValue
    private String imagePath;

    private String uppercaseTitle;
    private String weatherReport;

    @Self
    private SlingHttpServletRequest request;

    @ValueMapValue
    private String city;

    // @OSGiService
    // private WeatherServiceImpl weatherService;


    @PostConstruct
    protected void init() {
        // if (city != null && weatherService != null) {
        //     this.weatherReport = weatherService.getWeatherInfo(city);
        // }
        uppercaseTitle = StringUtils.isNotBlank(title) ? title.toUpperCase() : "Default Title here!";
        if (request != null) {
            this.searchQuery = request.getParameter("search");
        }
    }

    public String getUppercaseTitle() {
        return uppercaseTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public String getWeatherReport() {
        return weatherReport;
    }
}
