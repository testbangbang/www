package com.onyx.reader.host.navigation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuzeng on 10/18/15.
 */
public class SubScreenNavigationManager {

    public enum NavigationMode {
        VERTICAL_SCROLLING_MODE,
        SUBSCREEN_LIST_MODE,
        AUTO_CROP_PAGE_SINGLE_COLUMN,
        AUTO_CROP_WIDTH_SINGLE_COLUMN,
    }

    private Map<NavigationMode, NavigationProviderBase> provider = new HashMap<NavigationMode, NavigationProviderBase>();
    private NavigationMode currentProvider;

    public SubScreenNavigationManager() {
        super();
        provider.put(NavigationMode.VERTICAL_SCROLLING_MODE, new NavigationProviderBase());
        provider.put(NavigationMode.AUTO_CROP_WIDTH_SINGLE_COLUMN, null);
    }


}
