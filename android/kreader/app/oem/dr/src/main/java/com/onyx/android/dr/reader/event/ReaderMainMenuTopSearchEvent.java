package com.onyx.android.dr.reader.event;

/**
 * Created by huxiaomao on 17/5/10.
 */

public class ReaderMainMenuTopSearchEvent {
    private String selectionText;

    public ReaderMainMenuTopSearchEvent(String selectionText) {
        this.selectionText = selectionText;
    }

    public String getSelectionText() {
        return selectionText;
    }
}
