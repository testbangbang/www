package com.onyx.android.sdk.data.model.v2;

import java.io.Serializable;
import java.util.List;

/**
 * Created by li on 2017/9/18.
 */

public class CreateBookReportRequestBean implements Serializable{
    private String name;
    private String content;
    private String book;
    private List<CommentsBean> comments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public List<CommentsBean> getComments() {
        return comments;
    }

    public void setComments(List<CommentsBean> comments) {
        this.comments = comments;
    }
}
