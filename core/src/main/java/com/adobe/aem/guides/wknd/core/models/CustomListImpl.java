package com.adobe.aem.guides.wknd.core.models;

import com.adobe.aem.guides.wknd.core.services.PageViewCountService;
import com.adobe.aem.guides.wknd.core.services.SimpleGreetingService;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, adapters = {
        CustomList.class }, resourceType = {
                CustomListImpl.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CustomListImpl implements CustomList {
    protected static final String RESOURCE_TYPE = "wknd/components/content/custom-list-v1";
    protected static final String VIEW_COUNT_PROPERTY = "viewCount";
    @Self
    @Via(type = ResourceSuperType.class)
    private com.adobe.cq.wcm.core.components.models.List coreList;

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Page currentPage;

    private java.util.List<ListItem> paginatedItems = new ArrayList<>();
    private int currentPageValue = 1;
    private int totalPages = 1;
    private boolean hasNext = false;
    private boolean hasPrevious = false;
    private int previousPage;
    private int nextPage;
    private List<Integer> pageNumbers = new ArrayList<>();
    private ValueMap properties;

    @ValueMapValue
    @Default(intValues = 5)
    private int pageSize;

    @OSGiService
    private PageViewCountService viewCountService;

    @PostConstruct
    protected void init() {
        if (currentPage != null && currentPage.getContentResource() != null) {
            this.properties = currentPage.getContentResource().getValueMap();
        }
        this.previousPage = currentPageValue - 1;
        this.nextPage = currentPageValue + 1;

        if (coreList == null || coreList.getListItems() == null || pageSize <= 0) {
            return;
        }

        List<ListItem> allItems = new ArrayList<>(coreList.getListItems());
        int total = allItems.size();

        totalPages = (int) Math.ceil((double) total / pageSize);

        for (String sel : request.getRequestPathInfo().getSelectors()) {
            if (sel.matches("\\d+")) {
                currentPageValue = Integer.parseInt(sel);
            }
        }

        currentPageValue = Math.max(1, Math.min(currentPageValue, totalPages));

        int start = (currentPageValue - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        if (start < total) {
            paginatedItems = allItems.subList(start, end);

            ResourceResolver resolver = request.getResourceResolver();
            for (ListItem item : paginatedItems) {
                Resource itemContent = resolver.getResource(item.getPath() + "/jcr:content");
                if (itemContent != null) {
                    ModifiableValueMap mvm = itemContent.adaptTo(ModifiableValueMap.class);
                    if (mvm != null) {
                        int viewCount = mvm.get(VIEW_COUNT_PROPERTY, 0);
                        mvm.put(VIEW_COUNT_PROPERTY, viewCount + 1);
                    }
                }
            }
            try {
                resolver.commit();
            } catch (Exception e) {
            }
        }
        for (int i = 1; i <= totalPages; i++) {
            pageNumbers.add(i);
        }

        hasPrevious = currentPageValue > 1;
        hasNext = currentPageValue < totalPages;

    }

    public String getEnvironment() {
        return viewCountService.getEnvName();
    }

    public Collection<ListItem> getCustomListItems() {
        return paginatedItems;
    }

    public int getCurrentPage() {
        return currentPageValue;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public List<Integer> getPageNumbers() {
        return pageNumbers;
    }

    public int getPreviousPage() {
        return previousPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public int getViewCount() {
        return properties.get(VIEW_COUNT_PROPERTY, 0);
    }

    private int getViewCountForItem(ListItem item) {
        Resource itemResource = request.getResourceResolver().getResource(item.getPath());
        if (itemResource != null) {
            return itemResource.getValueMap().get(VIEW_COUNT_PROPERTY, 0);
        }
        return 0;
    }
}