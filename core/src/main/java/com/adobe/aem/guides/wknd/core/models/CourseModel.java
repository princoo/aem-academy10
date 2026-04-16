package com.adobe.aem.guides.wknd.core.models;

public class CourseModel {
    private final String id;
    private final String title;
    private final String description;
    private final String startDate;
    private final String thumbnail;
    private final String[] tags;

    public CourseModel(String id, String title, String description, String startDate, String thumbnail, String[] tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.thumbnail = thumbnail;
        this.tags = tags;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStartDate() { return startDate; }
    public String getThumbnail() { return thumbnail; }
    public String[] getTags() { return tags; }
}