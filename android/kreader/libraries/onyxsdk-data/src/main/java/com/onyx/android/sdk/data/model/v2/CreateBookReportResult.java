package com.onyx.android.sdk.data.model.v2;

import java.util.Date;
import java.util.List;

/**
 * Created by li on 2017/9/18.
 */

public class CreateBookReportResult {
    public String _id;
    public int __v;
    public Date updatedAt;
    public Date createdAt;
    public String content;
    public String name;
    public String book;
    public String user;
    public List<CommentsBean> comments;

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
