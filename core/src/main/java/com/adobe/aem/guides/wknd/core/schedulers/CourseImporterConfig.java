package com.adobe.aem.guides.wknd.core.schedulers;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "WKND Course Importer Configuration", description = "Configures the scheduled job to import courses from a CSV file.")
public @interface CourseImporterConfig {

    @AttributeDefinition(name = "Cron Expression", description = "Cron expression for the scheduler. Default is every day at 2 AM (0 0 2 * * ?)")
    String scheduler_expression() default "0 0 2 * * ?";

    @AttributeDefinition(name = "CSV File Path", description = "Path to the CSV file in the DAM")
    String csv_path() default "/content/dam/wknd/data/courses.csv";

    @AttributeDefinition(name = "Parent Page Path", description = "Where should the new courses be created?")
    String parent_path() default "/content/wknd/courses";
}