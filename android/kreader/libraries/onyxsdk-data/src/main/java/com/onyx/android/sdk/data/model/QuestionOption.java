package com.onyx.android.sdk.data.model;

import java.io.Serializable;

/**
 * Created by lxm on 2017/10/31.
 */

public class QuestionOption implements Serializable {

    public String value;
    public boolean checked;
    public String _id;

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
