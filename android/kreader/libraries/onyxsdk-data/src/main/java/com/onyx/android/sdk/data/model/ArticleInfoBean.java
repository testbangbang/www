package com.onyx.android.sdk.data.model;

import java.io.Serializable;

/**
 * Created by zhouzhiming on 2017/11/27.
 */
public class ArticleInfoBean implements Serializable {
    public int __v;
    public String _id;
    public String content;
    public String createdAt;
    public ArticleBookBean target;
    public ArticleLibraryBean library;
    public String title;
    public String updatedAt;
    public boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
    }
}
