package com.onyx.edu.reader.ui.events;

import com.onyx.android.sdk.scribble.data.TouchPointList;

/**
 * Created by zhuzeng on 28/10/2016.
 */

public class ShortcutErasingEvent {
    private TouchPointList list;

    public ShortcutErasingEvent(final TouchPointList list) {
        this.list = list;
    }

    public final TouchPointList getTouchPointList() {
        return list;
    }
}
