package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.model.v2.CommentsBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/9/20.
 */
public class CreateInformalSecondBean implements Serializable {
    public int __v;
    public String updatedAt;
    public String createdAt;
    public String _id;
    public String title;
    public String wordNumber;
    public String user;
    public String content;
    public long currentTime;
    public List<CommentsBean> comments;
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
