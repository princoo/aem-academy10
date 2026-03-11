package com.adobe.aem.guides.wknd.core.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TeamMemberModel {

    @ValueMapValue
    private String name;

    @ValueMapValue
    private String role;

    public String getName() {
        return StringUtils.isNotBlank(name) ? name.toUpperCase() : null;
    }

    public String getRole() {
        return role;
    }
}