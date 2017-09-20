package com.onyx.android.sdk.data.model.v2;

import java.util.Date;
import java.util.List;

/**
 * Created by li on 2017/9/19.
 */

public class GetBookReportListBean {
    public String _id;
    public Date updatedAt;
    public Date createdAt;
    public String name;
    public String content;
    public String book;
    public BookReportUserBean user;
    public int __v;
    public List<CommentsBean> comments;
}
