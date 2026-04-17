package com.adobe.aem.guides.wknd.core.services;

import java.util.List;

public class PropertyUpdateResult {

    private final boolean changed;
    private final List<String> updatedProperties;

    public PropertyUpdateResult(boolean changed, List<String> updatedProperties) {
        this.changed = changed;
        this.updatedProperties = updatedProperties;
    }

    public boolean isChanged() {
        return changed;
    }

    public List<String> getUpdatedProperties() {
        return updatedProperties;
    }
}
