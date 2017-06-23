package com.onyx.android.sdk.reader.api;

import com.onyx.android.sdk.utils.Debug;

/**
 * Created by joy on 5/22/17.
 */

public class ReaderFormRadioButton extends ReaderFormField {

    private boolean checked;

    private ReaderFormRadioButton(float left, float top, float right, float bottom,
                               boolean checked) {
        super("", left, top, right, bottom);
        this.checked = checked;
    }

    public static ReaderFormRadioButton create(float left, float top, float right, float bottom,
                                            boolean checked) {
        return new ReaderFormRadioButton(left, top, right, bottom, checked);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
