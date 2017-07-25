package com.onyx.einfo.model;

import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by suicheng on 2017/7/20.
 */

public class TabLibrary {
    public Library library;
    public String tabTitle;
    public TabAction action = TabAction.None;

    public TabLibrary(Library library) {
        this.library = library;
    }

    public String getTabTitle() {
        if (library == null || StringUtils.isNullOrEmpty(library.getName())) {
            return String.valueOf(tabTitle);
        }
        return library.getName();
    }
}
