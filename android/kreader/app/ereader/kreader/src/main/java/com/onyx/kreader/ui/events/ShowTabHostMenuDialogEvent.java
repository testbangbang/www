package com.onyx.kreader.ui.events;

/**
 * Created by zhuzeng on 7/29/16.
 */
public class ShowTabHostMenuDialogEvent {
    public int dialogWindowLeft;
    public int dialogWindowTop;

    public ShowTabHostMenuDialogEvent(int x, int y) {
        this.dialogWindowLeft = x;
        this.dialogWindowTop = y;
    }
}
