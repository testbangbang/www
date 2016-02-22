package com.onyx.kreader.common;

import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.host.math.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by zengzhu on 2/22/16.
 */
public class AdditionalInfo {

    private List<PageInfo> visiblePages;
    private List<ReaderSelection> selectionList;
    private Map<String, Boolean> history;
    public int specialScale = 0;
    public float actualScale;

    public final List<PageInfo> getVisiblePages() {
        return visiblePages;
    }

    public final List<ReaderSelection> getSelectionList() {
        return selectionList;
    }

}
