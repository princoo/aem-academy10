package com.adobe.aem.guides.wknd.core.util;

import org.apache.sling.api.resource.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JcrUtils {

    private static final Logger logger = LoggerFactory.getLogger(JcrUtils.class);

    public static boolean setAtomicProperty(ResourceResolver resolver, Resource parent, String nodeName,
            String propName,
            Object newValue, String resourceType) {
        try {
            Resource child = parent.getChild(nodeName);
            boolean isModified = false;
            if (child == null) {
                Map<String, Object> props = new HashMap<>();
                props.put("jcr:primaryType", "nt:unstructured");
                props.put("sling:resourceType", resourceType);
                child = resolver.create(parent, nodeName, props);
                isModified = true;
            }
            ModifiableValueMap mvm = child.adaptTo(ModifiableValueMap.class);
            if (mvm != null && newValue != null) {
                Object oldValue = mvm.get(propName);
                if (!Objects.deepEquals(oldValue, newValue)) {
                    mvm.put(propName, newValue);
                    isModified = true;
                }
            }
            return isModified;
        } catch (PersistenceException e) {
            logger.error("Could not update property {} on node {}", propName, nodeName, e);
            return false;
        }
    }
}