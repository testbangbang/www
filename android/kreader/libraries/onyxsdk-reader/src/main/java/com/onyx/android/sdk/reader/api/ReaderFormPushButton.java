package com.onyx.android.sdk.reader.api;

import com.onyx.android.sdk.utils.Debug;

/**
 * Created by joy on 5/22/17.
 */

public class ReaderFormPushButton extends ReaderFormField {
    private String caption;

    private ReaderFormPushButton(String name, String caption, float left, float top, float right, float bottom) {
        super(name, left, top, right, bottom);
        this.caption = caption;
    }

    public static ReaderFormPushButton create(String name, String caption, float left, float top, float right, float bottom) {
        Debug.e(ReaderFormPushButton.class, "create: " + name + ", " + ", " + caption + ", " + left + ", " + top + ", " + right + ", " + bottom);
        return new ReaderFormPushButton(name, caption, left, top, right, bottom);
    }

    public String getCaption() {
        return caption;
    }
}
