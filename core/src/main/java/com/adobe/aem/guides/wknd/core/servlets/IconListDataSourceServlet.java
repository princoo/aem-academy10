package com.adobe.aem.guides.wknd.core.servlets;

import com.adobe.aem.guides.wknd.core.util.IconProvider;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(resourceTypes = "wknd/datasources/icon-list", methods = "GET")
public class IconListDataSourceServlet extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        List<Resource> resourceList = new ArrayList<>();

        Map<String, String> icons = IconProvider.getIcons();

        for (Map.Entry<String, String> icon : icons.entrySet()) {
            ValueMap vm = new ValueMapDecorator(new HashMap<>());
            vm.put("value", icon.getKey());
            vm.put("text", icon.getValue());

            Resource iconResource = new ValueMapResource(resourceResolver, new ResourceMetadata(), "nt:unstructured",
                    vm);
            resourceList.add(iconResource);
        }
        DataSource dataSource = new SimpleDataSource(resourceList.iterator());
        request.setAttribute(DataSource.class.getName(), dataSource);

    }
}