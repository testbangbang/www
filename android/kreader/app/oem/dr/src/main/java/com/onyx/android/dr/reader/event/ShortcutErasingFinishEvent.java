package com.onyx.android.dr.reader.event;

import com.onyx.android.sdk.scribble.data.TouchPointList;

/**
 * Created by zhuzeng on 21/10/2016.
 */

public class ShortcutErasingFinishEvent {
    private TouchPointList list;
    public ShortcutErasingFinishEvent(final TouchPointList list) {
        this.list = list;
    }

    public final TouchPointList getTouchPointList() {
        return list;
    }
}
