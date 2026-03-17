package com.adobe.aem.guides.wknd.core.servlets;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.crx.JcrConstants;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.util.*;

@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "= Dynamic Drop Down",
        "sling.servlet.resourceTypes=" + "/apps/dropDownLIsting"
})
public class DropDownServlet extends SlingSafeMethodsServlet {

    private static Logger LOGGER = LoggerFactory.getLogger(DropDownServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request,
            SlingHttpServletResponse response) {
        try {
            ResourceResolver resourceResolver = request.getResourceResolver();
            List<KeyValue> dropDownList = new ArrayList<>();
            Resource pathResource = request.getResource();
            Resource dataSource = pathResource.getChild("datasource");
            if (dataSource == null) {
                return;
            }
            String rootPath = dataSource.getValueMap().get("rootPath", String.class);

            if (rootPath == null) {
                return;
            }
            Resource resource = resourceResolver.getResource(rootPath);
            if(resource == null){
                return;
            }
            Page page = resource.adaptTo(Page.class);
            if(page != null){
                Iterator<Page> iterator = page.listChildren();
                while (iterator.hasNext()) {
                    Page childPage = iterator.next();
                    dropDownList.add(new KeyValue(childPage.getPath(), childPage.getTitle()));
                }
            }
            @SuppressWarnings("unchecked")
            DataSource ds = new SimpleDataSource(
                    new TransformIterator(
                            dropDownList.iterator(),
                            input -> {
                                KeyValue keyValue = (KeyValue) input;
                                ValueMap vm = new ValueMapDecorator(new HashMap<>());
                                vm.put("value", keyValue.key);
                                vm.put("text", keyValue.value);
                                return new ValueMapResource(
                                        resourceResolver, new ResourceMetadata(),
                                        JcrConstants.NT_UNSTRUCTURED, vm);
                            }));
            request.setAttribute(DataSource.class.getName(), ds);

        } catch (Exception e) {
            LOGGER.error("Error in Get Drop Down Values", e);
        }
    }

    private class KeyValue {

        /**
         * key property.
         */
        private String key;
        /**
         * value property.
         */
        private String value;

        /**
         * constructor instance intance.
         *
         * @param newKey   -
         * @param newValue -
         */
        private KeyValue(final String newKey, final String newValue) {
            this.key = newKey;
            this.value = newValue;
        }
    }
}