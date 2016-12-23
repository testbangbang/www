package com.onyx.kreader.ui.events;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class ChangeCodePageEvent {
    private int codePage;

    public ChangeCodePageEvent(int codePage) {
        this.codePage = codePage;
    }

    public int getCodePage() {
        return codePage;
    }
}
