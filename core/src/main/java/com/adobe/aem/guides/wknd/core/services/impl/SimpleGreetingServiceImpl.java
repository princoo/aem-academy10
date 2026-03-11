package com.adobe.aem.guides.wknd.core.services.impl;

import org.osgi.service.component.annotations.Component;

import com.adobe.aem.guides.wknd.core.services.SimpleGreetingService;

@Component(service = SimpleGreetingService.class, immediate = true)
public class SimpleGreetingServiceImpl implements SimpleGreetingService {

    @Override
    public String getGreeting(String name) {
        return "hello, " + name + "! welcome to rwandaa";
    }
}