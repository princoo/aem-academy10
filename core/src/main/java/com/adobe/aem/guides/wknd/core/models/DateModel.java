package com.adobe.aem.guides.wknd.core.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DateModel {

    @ValueMapValue
    private Calendar date;

    public String getFormattedDate() {
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
            return formatter.format(date.getTime());
        }
        return "no date selected";
    }
}