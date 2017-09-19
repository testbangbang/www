package com.onyx.android.sdk.data.model.v2;

import java.util.Date;
import java.util.List;

/**
 * Created by li on 2017/9/18.
 */

public class CreateBookReportResult {
    private int __v;
    private Date updatedAt;
    private Date createdAt;
    private String content;
    private String name;
    private String book;
    private String user;
    private List<CommentsBean> comments;

    public int get__v() {
        return __v;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public String getBook() {
        return book;
    }

    public String getUser() {
        return user;
    }

    public List<CommentsBean> getComments() {
        return comments;
    }
}
