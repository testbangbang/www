package com.onyx.kreader.common;

import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.options.ReaderConstants;

import java.util.List;
import java.util.Map;

/**
 * Created by zengzhu on 2/22/16.
 */
public class AdditionalInfo {

    private List<PageInfo> visiblePages;
    private List<ReaderSelection> selectionList;
    public boolean canGoBack;
    public boolean canGoForward;
    public int specialScale = ReaderConstants.SCALE_INVALID;
    public float actualScale;
    public boolean reflowDocument;
    public boolean pageScalable;

    public final List<PageInfo> getVisiblePages() {
        return visiblePages;
    }

    public final List<ReaderSelection> getSelectionList() {
        return selectionList;
    }

}
