package com.onyx.android.sdk.reader.api;

import com.onyx.android.sdk.utils.Debug;

/**
 * Created by joy on 5/22/17.
 */

public class ReaderFormScribble extends ReaderFormField {

    private ReaderFormScribble(String name, float left, float top, float right, float bottom) {
        super(name, left, top, right, bottom);
    }

    public static ReaderFormScribble create(String name, float left, float top, float right, float bottom) {
        return new ReaderFormScribble(name, left, top, right, bottom);
    }

}
