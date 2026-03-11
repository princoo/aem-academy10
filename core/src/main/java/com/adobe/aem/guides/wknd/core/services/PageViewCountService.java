package com.adobe.aem.guides.wknd.core.services;

import org.apache.sling.api.resource.Resource;
public interface PageViewCountService {
    void increment(Resource jcrContentResource);
    int getPageSize();
    String getEnvName();
}
