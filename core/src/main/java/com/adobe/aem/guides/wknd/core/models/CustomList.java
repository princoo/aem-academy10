package com.adobe.aem.guides.wknd.core.models;

import java.util.Collection;

import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;

public interface CustomList extends List {

    Collection<ListItem> getCustomListItems();

    int getCurrentPage();

    int getTotalPages();

    boolean isHasNext();

    boolean isHasPrevious();

    interface ListAdvancedItem extends CustomList {
        String getDescription();
    }

}
