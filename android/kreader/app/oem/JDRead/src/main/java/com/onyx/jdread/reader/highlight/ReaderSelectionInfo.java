package com.onyx.jdread.reader.highlight;

import com.onyx.android.sdk.reader.api.ReaderSelection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2018/1/16.
 */

public class ReaderSelectionInfo {
    private ReaderSelection currentSelection;
    private List<HighlightCursor> cursors = new ArrayList<HighlightCursor>();

    public ReaderSelection getCurrentSelection() {
        return currentSelection;
    }

    public void setCurrentSelection(ReaderSelection currentSelection) {
        this.currentSelection = currentSelection;
    }

    public List<HighlightCursor> getCursors() {
        return cursors;
    }

    public void setCursors(List<HighlightCursor> cursors) {
        this.cursors.clear();
        this.cursors.addAll(cursors);
    }
}