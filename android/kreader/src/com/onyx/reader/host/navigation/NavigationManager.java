package com.onyx.reader.host.navigation;

import android.graphics.RectF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuzeng on 10/18/15.
 */
public class NavigationManager {

    public enum NavigationMode {
        SINGLE_PAGE_SCALE_MODE,
        SINGLE_PAGE_SUBSCREEN_MODE,
        SINGLE_PAGE_CROP_PAGE_MODE,
        SINGLE_PAGE_CROP_WIDTH_MODE,
        AUTO_CROP_PAGE_SINGLE_COLUMN,
        AUTO_CROP_WIDTH_SINGLE_COLUMN,
    }

    static public class NavigationArgs {
        public NavigationMode mode;
        public List<RectF> list;
        public float scale;
        public RectF limit;
    }

    private Map<NavigationMode, NavigationProviderBase> provider = new HashMap<NavigationMode, NavigationProviderBase>();
    private NavigationMode currentProvider;

    public NavigationManager() {
        super();
        provider.put(NavigationMode.SINGLE_PAGE_SCALE_MODE, new NavigationProviderBase());
        provider.put(NavigationMode.AUTO_CROP_WIDTH_SINGLE_COLUMN, null);
    }


}
