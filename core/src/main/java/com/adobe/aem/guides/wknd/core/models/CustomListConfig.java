package com.adobe.aem.guides.wknd.core.models;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "WKND Custom List Configuration")
public @interface CustomListConfig {
    @AttributeDefinition(name = "Page Size", description = "Number of items to show")
    int pageSize() default 5;

    @AttributeDefinition(name = "Environment Name")
    String environment() default "Production";
}