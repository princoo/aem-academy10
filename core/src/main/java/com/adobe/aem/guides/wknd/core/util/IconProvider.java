package com.adobe.aem.guides.wknd.core.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class IconProvider {
    private static final Map<String, String> ICONS = new LinkedHashMap<>();
    static {
        ICONS.put("", "No Icon");
        ICONS.put("icon-academy-account_balance", "Account Balance");
        ICONS.put("icon-academy-arrow_forward", "Arrow Forward");
        ICONS.put("icon-academy-cell_merge", "Cell Merge");
        ICONS.put("icon-academy-close", "Close");
        ICONS.put("icon-academy-download", "Download");
        ICONS.put("icon-academy-graph_5", "Graph 5");
        ICONS.put("icon-academy-help", "Help");
        ICONS.put("icon-academy-menu", "Menu");
        ICONS.put("icon-academy-receipt_long", "Receipt Long");
        ICONS.put("icon-academy-search", "Search");
        ICONS.put("icon-academy-school", "School");
        ICONS.put("icon-academy-Vector", "Vector");
        ICONS.put("icon-academy-arrows_output", "Arrows Output");
        ICONS.put("icon-academy-Group-1171274760", "Group 1171274760");

    }

    public static Map<String, String> getIcons() {
        return ICONS;
    }
}