package com.adobe.aem.guides.wknd.core.services;

public interface CourseImporterService {

    String importCourses(String csvPath, String parentPath);
}