package com.onyx.knote.scribble.event;

import com.onyx.android.sdk.ui.dialog.DialogCustomLineWidth;

/**
 * Created by solskjaer49 on 2017/7/20 18:02.
 */

public class CustomWidthEvent {
    public CustomWidthEvent(DialogCustomLineWidth.Callback callBack) {
        doneCallBack = callBack;
    }

    public DialogCustomLineWidth.Callback getDoneCallBack() {
        return doneCallBack;
    }

    private DialogCustomLineWidth.Callback doneCallBack;

}
