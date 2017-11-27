package com.onyx.android.sdk.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/11/27.
 */
public class ArticleBookBean implements Serializable {
    public List<String> authors;
    public Date createdAt;
    public String entity;
    public String ref;
    public String title;
}
