package com.onyx.android.sdk.data.request.cloud;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Comment;
import com.onyx.android.sdk.data.model.ProductQuery;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class CommentListRequest extends BaseCloudRequest {

    private String bookId;
    private ProductQuery query;
    private ProductResult<Comment> productResult;

    public CommentListRequest(String bookId, ProductQuery query) {
        this.bookId = bookId;
        this.query = query;
    }

    public ProductResult<Comment> getProductResult() {
        return productResult;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<ProductResult<Comment>> response = executeCall(ServiceFactory.getBookStoreService(parent.getCloudConf().getApiBase())
                .getBookCommentList(bookId, JSON.toJSONString(query)));
        if (response.isSuccessful()) {
            productResult = response.body();
        }
    }
}
