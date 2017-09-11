package com.onyx.kreader.note.event;

import com.onyx.android.sdk.scribble.data.TouchPointList;

/**
 * Created by joy on 8/31/17.
 */

public class RawErasingFinishEvent {
    public TouchPointList list;

    public RawErasingFinishEvent(TouchPointList l) {
        list = l;
    }
}
