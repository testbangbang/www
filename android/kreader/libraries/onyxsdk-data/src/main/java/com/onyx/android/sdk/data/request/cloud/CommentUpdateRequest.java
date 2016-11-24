package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Comment;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class CommentUpdateRequest extends BaseCloudRequest {

    private String bookId;
    private String commentId;
    private Comment comment;
    private Comment resultComment;

    public CommentUpdateRequest(String bookId, String commentId, Comment comment) {
        this.bookId = bookId;
        this.commentId = commentId;
        this.comment = comment;
    }

    public Comment getResultComment() {
        return resultComment;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<Comment> response = executeCall(ServiceFactory.getBookStoreService(parent.getCloudConf().getApiBase())
                .updateBookComment(bookId, commentId, comment, getAccountSessionToken()));
        if (response.isSuccessful()) {
            resultComment = response.body();
        }
    }
}
