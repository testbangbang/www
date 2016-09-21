package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Comment;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class CommentSupportRequest extends BaseCloudRequest {

    private String bookId;
    private String commentId;
    private Comment comment;

    public CommentSupportRequest(String bookId, String commentId) {
        this.bookId = bookId;
        this.commentId = commentId;
    }

    public Comment getComment() {
        return comment;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<Comment> response = ServiceFactory.getBookStoreService(parent.getCloudConf().getApiBase())
                .supportBookComment(bookId, commentId).execute();
        if (response.isSuccessful()) {
            comment = response.body();
        }
    }
}
