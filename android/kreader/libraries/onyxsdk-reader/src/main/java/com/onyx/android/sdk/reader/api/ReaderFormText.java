package com.onyx.android.sdk.reader.api;

import com.onyx.android.sdk.utils.Debug;

/**
 * Created by joy on 5/22/17.
 */

public class ReaderFormText extends ReaderFormField {
    private String text;

    private ReaderFormText(String name, float left, float top, float right, float bottom,
                           String defaultText) {
        super(name, left, top, right, bottom);
        text = defaultText;
    }

    public static ReaderFormField create(String name, float left, float top, float right, float bottom,
                                 String defaultText) {
        ReaderFormType formType = ReaderFormField.getFormTypeByName(name);
        if (formType == ReaderFormType.SCRIBBLE) {
            return ReaderFormScribble.create(name, left, top, right, bottom);
        }
        return new ReaderFormText(name, left, top, right, bottom, defaultText);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
