package com.adobe.aem.guides.wknd.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component(service = Servlet.class)
@SlingServletResourceTypes(
    resourceTypes = "wknd/components/content/custom-list-v1", 
    methods = "GET", 
    selectors = "loadmore", 
    extensions = "json"
)
public class CustomLoadList extends SlingSafeMethodsServlet {
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        
        Resource resource = request.getResource();
        ValueMap properties = resource.getValueMap();
        String rootPath = properties.get("parentPage", String.class);
        if (rootPath == null) {
            response.sendError(400, "Component is missing the rootPath property.");
            return;
        }

        int pageNum = 1;
        String[] selectors = request.getRequestPathInfo().getSelectors();
        for (String s : selectors) {
            if (s.matches("\\d+")) {
                pageNum = Integer.parseInt(s);
            }
        }

        PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
        Page rootPage = pageManager.getPage(rootPath);
        
        List<Page> allPages = new ArrayList<>();
        if (rootPage != null) {
            Iterator<Page> children = rootPage.listChildren();
            while (children.hasNext()) {
                allPages.add(children.next());
            }
        }

        int pageSize = properties.get("pageSize", 5);
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allPages.size());

        JsonArray itemsArray = new JsonArray();
        
        if (start < allPages.size()) {
            List<Page> paginatedList = allPages.subList(start, end);
            
            for (Page page : paginatedList) {
                JsonObject item = new JsonObject();
                item.addProperty("title", page.getTitle());
                item.addProperty("url", page.getPath() + ".html");
                item.addProperty("description", page.getDescription());
                itemsArray.add(item);
            }
        }

        JsonObject finalResponse = new JsonObject(); 
        finalResponse.add("items", itemsArray);
        finalResponse.addProperty("hasNext", end < allPages.size());
        finalResponse.addProperty("nextPage", pageNum + 1);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(finalResponse.toString());
    }
}