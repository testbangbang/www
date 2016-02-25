package com.onyx.kreader.common;

import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.options.ReaderConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zengzhu on 2/22/16.
 */
public class ReaderViewInfo {

    private List<PageInfo> visiblePages = new ArrayList<PageInfo>();
    private Map<String, List<ReaderSelection>> selectionMap = new HashMap<String, List<ReaderSelection>>();
    public boolean canGoBack;
    public boolean canGoForward;
    public boolean supportReflow;
    public boolean supportScalable;

    public final List<PageInfo> getVisiblePages() {
        return visiblePages;
    }

    public void copyPageInfo(final PageInfo pageInfo) {
        PageInfo copy = new PageInfo(pageInfo);
        visiblePages.add(copy);
    }

    public final List<ReaderSelection> getSelectionList(final String type) {
        return selectionMap.get(type);
    }

}
