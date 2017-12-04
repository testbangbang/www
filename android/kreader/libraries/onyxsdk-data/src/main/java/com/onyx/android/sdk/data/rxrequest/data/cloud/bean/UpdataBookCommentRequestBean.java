package com.onyx.android.sdk.data.rxrequest.data.cloud.bean;

import com.onyx.android.sdk.data.model.Comment;

/**
 * Created by jackdeng on 2017/11/13.
 */

public class UpdataBookCommentRequestBean {
    public String bookId;
    public String commentId;
    public String sessionToken;
    public Comment comment;
}