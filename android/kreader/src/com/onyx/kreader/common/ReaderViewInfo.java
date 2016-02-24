package com.onyx.kreader.common;

import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.options.ReaderConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zengzhu on 2/22/16.
 */
public class ReaderViewInfo {

    private List<PageInfo> visiblePages = new ArrayList<PageInfo>();
    private List<ReaderSelection> selectionList = new ArrayList<ReaderSelection>();
    public boolean canGoBack;
    public boolean canGoForward;
    public int specialScale = ReaderConstants.SCALE_INVALID;
    public float actualScale;
    public boolean reflowDocument;
    public boolean pageScalable;

    public final List<PageInfo> getVisiblePages() {
        return visiblePages;
    }

    public void copyPageInfo(final PageInfo pageInfo) {
        PageInfo copy = new PageInfo(pageInfo);
        visiblePages.add(copy);
    }

    public final List<ReaderSelection> getSelectionList() {
        return selectionList;
    }

}
