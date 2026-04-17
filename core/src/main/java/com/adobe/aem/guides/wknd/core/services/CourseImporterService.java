package com.adobe.aem.guides.wknd.core.services;

import org.apache.sling.api.resource.ResourceResolver;

public interface CourseImporterService {

    String importCourses(String csvPath, String parentPath);
    void processCourseImportTask(ResourceResolver resolver, String csvPath, String parentPath, String reportPath, long startTime, String initiatingUser);
}