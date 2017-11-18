package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.model.UserBean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by li on 2017/9/19.
 */

public class GetBookReportListBean implements Serializable{
    public String _id;
    public Date updatedAt;
    public Date createdAt;
    public String name;
    public String content;
    public String book;
    public UserBean user;
    public String pageNumber;
    public String title;
    public int __v;
    public List<CommentsBean> comments;
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
