package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.model.ArticleInfoBean;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/11/27.
 */
public class GetArticlePushBean {
    public String _id;
    public List<String> ancestors;
    public List<String> category;
    public int childrenCount;
    public int count;
    public String createdAt;
    public String idString;
    public String name;
    public Object onyxOptions;
    public int ordinal;
    public int status;
    public String updatedAt;
    public List<ArticleInfoBean> list;
}
