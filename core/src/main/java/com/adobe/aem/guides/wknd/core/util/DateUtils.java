package com.adobe.aem.guides.wknd.core.util;

import org.apache.commons.lang3.time.FastDateFormat;
import java.util.Calendar;

public class DateUtils {

    private static final String DEFAULT_FORMAT = "MMMM dd, yyyy";
    private static final FastDateFormat DATE_FORMATTER = FastDateFormat.getInstance(DEFAULT_FORMAT);

    public static String formatCourseDate(Calendar calendar) {
        if (calendar == null) {
            return "Date TBD";
        }
        return DATE_FORMATTER.format(calendar.getTime());
    }

    public static String formatCustom(Calendar calendar, String pattern) {
        if (calendar == null) return "";
        return FastDateFormat.getInstance(pattern).format(calendar.getTime());
    }
}