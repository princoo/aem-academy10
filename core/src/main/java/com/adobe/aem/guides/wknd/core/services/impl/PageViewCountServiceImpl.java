package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.models.CustomListConfig;
import com.adobe.aem.guides.wknd.core.services.PageViewCountService;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = PageViewCountService.class, immediate = true)
@Designate(ocd = CustomListConfig.class)
public class PageViewCountServiceImpl implements PageViewCountService {
    private int pageSize;
    private String envName;

    @Activate
    @Modified
    protected void activate(CustomListConfig config) {
        this.pageSize = config.pageSize();
        this.envName = config.environment();
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }
    @Override 
    public String getEnvName() {
        return envName;
    }
    @Override
    public void increment(Resource jcrContentResource) {
        try {
            ModifiableValueMap properties = jcrContentResource.adaptTo(ModifiableValueMap.class);

            if (properties != null) {
                int currentCount = properties.get("viewCount", 0);

                properties.put("viewCount", currentCount + 1);

                jcrContentResource.getResourceResolver().commit();
            }
        } catch (Exception e) {}
    }
}