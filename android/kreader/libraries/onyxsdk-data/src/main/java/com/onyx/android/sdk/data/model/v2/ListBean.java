package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.model.ChildBean;

/**
 * Created by zhouzhiming on 2017/9/20.
 */
public class ListBean {
    public String _id;
    public String createdAt;
    public String ref;
    public String updatedAt;
    public String parent;
    public ChildBean child;
    public boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
