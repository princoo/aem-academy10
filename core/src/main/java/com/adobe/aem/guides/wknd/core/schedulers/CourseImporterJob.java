package com.adobe.aem.guides.wknd.core.schedulers;

import com.adobe.aem.guides.wknd.core.services.CourseImporterService;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = CourseImporterJob.class, immediate = true)
@Designate(ocd = CourseImporterConfig.class)
public class CourseImporterJob implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String JOB_NAME = "WKND_Course_Importer_Job";

    @Reference
    private Scheduler scheduler;

    @Reference
    private CourseImporterService courseImporterService;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private String csvPath;
    private String parentPath;
    private boolean enabled;

    @Activate
    protected void activate(CourseImporterConfig config) {
        this.csvPath = config.csv_path();
        this.parentPath = config.parent_path();
        this.enabled = config.service_enabled();

        ScheduleOptions options = scheduler.EXPR(config.scheduler_expression());
        options.name(JOB_NAME);
        options.canRunConcurrently(false);
        scheduler.schedule(this, options);
    }

    @Deactivate
    protected void deactivate() {
        scheduler.unschedule(JOB_NAME);
    }

    @Override
    public void run() {
        if (!enabled) {
            logger.info(
                    "COURSE IMPORTER: Scheduler triggered the job, but it is currently DISABLED. Skipping execution.");
            return;
        }
        logger.info("COURSE IMPORTER: Scheduler triggered the job");
        String status = courseImporterService.importCourses(csvPath, parentPath);
        logger.info("COURSE IMPORTER: Scheduled job finished. Status: {}", status);
    }
}