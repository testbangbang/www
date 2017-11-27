package com.onyx.android.dr.presenter;

import com.alibaba.fastjson.JSON;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.bean.MemberParameterBean;
import com.onyx.android.dr.data.ArticlePushData;
import com.onyx.android.dr.interfaces.ArticlePushView;
import com.onyx.android.dr.request.cloud.DeleteArticlePushRequest;
import com.onyx.android.dr.request.cloud.RequestGetArticlePush;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.ArticleInfoBean;

import java.util.List;

/**
 * Created by hehai on 17-9-20.
 */

public class ArticlePushPresenter {
    private ArticlePushView articlePushView;
    private ArticlePushData articlePushData;
    private String offset = "0";
    private String limit = "200";
    private String sortBy = "createdAt";
    private String order = "-1";

    public ArticlePushPresenter(ArticlePushView articlePushView) {
        this.articlePushView = articlePushView;
        articlePushData = new ArticlePushData();
    }

    public void getArticleList() {
        MemberParameterBean bean = new MemberParameterBean(offset, limit, sortBy, order);
        String json = JSON.toJSON(bean).toString();
        String id = DRPreferenceManager.loadUserLibraryId(DRApplication.getInstance(), "");
        final RequestGetArticlePush req = new RequestGetArticlePush(json, id);
        articlePushData.getArticlePush(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                articlePushView.setDataList(req.getGroup());
            }
        });
    }

    public void removeArticle(List<ArticleInfoBean> selectedList) {
        for (ArticleInfoBean entity : selectedList) {
            deleteArticlePush(entity._id);
        }
        getArticleList();
    }

    public void deleteArticlePush(String id) {
        final DeleteArticlePushRequest rq = new DeleteArticlePushRequest(id);
        articlePushData.removeArticlePush(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

}
