package com.adobe.aem.guides.wknd.core.models;

import java.util.Calendar;
import java.util.List;

public class CourseItem {
    private String title;
    private String image;
    private String description;
    private String link;
    private String startDate;
    private Calendar rawDate;
    private List<String> tags;

    public CourseItem(String title, String image, String description, String link, String startDate,
            Calendar rawDate,
            List<String> tags) {
        this.title = title;
        this.image = image;
        this.description = description;
        this.link = link;
        this.startDate = startDate;
        this.tags = tags;
        this.rawDate = rawDate;

    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getLink() {
        return link;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getTags() {
        return tags;
    }

    public Calendar getRawDate() {
        return rawDate;
    }
}