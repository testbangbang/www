package com.onyx.android.sdk.reader.api;

import com.onyx.android.sdk.utils.Debug;

/**
 * Created by joy on 5/22/17.
 */

public class ReaderFormCheckbox extends ReaderFormField {
    private boolean checked;

    private ReaderFormCheckbox(String name, float left, float top, float right, float bottom,
                              boolean checked) {
        super(name, left, top, right, bottom);
        this.checked = checked;
    }

    public static ReaderFormCheckbox create(String name, float left, float top, float right, float bottom,
                                            boolean checked) {
        Debug.e(ReaderFormCheckbox.class, "create: " + name + ", " + left + ", " + top + ", " + right + ", " + bottom);
        return new ReaderFormCheckbox(name, left, top, right, bottom, checked);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
