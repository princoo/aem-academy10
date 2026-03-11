package com.adobe.aem.guides.wknd.core.models;

import java.util.Collections;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class TeamListModel {

    @ChildResource(name = "teamMembers")
    private List<TeamMemberModel> members;

    public List<TeamMemberModel> getMembers() {
        if (members != null) {
            return members;
        }
        return Collections.emptyList();
    }

    public boolean isEmpty() {
        return members == null || members.isEmpty();
    }
}