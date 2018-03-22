package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.shop.cloud.entity.GetChapterGroupInfoRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetChaptersContentResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestGetChaptersContent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jackdeng on 2017/12/13.
 */

public class GetChaptersContentAction extends BaseAction<ShopDataBundle> {

    private long bookID;
    public String bookName;
    public String localPath;
    public String type;
    public String ids;
    public boolean can_try;
    private GetChaptersContentResultBean resultBean;

    public GetChaptersContentAction(long bookID, String bookName, String type, String ids, boolean can_try) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.type = type;
        this.ids = ids;
        this.can_try = can_try;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public GetChaptersContentResultBean getResultBean() {
        return resultBean;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        GetChapterGroupInfoRequestBean baseRequestBean = new GetChapterGroupInfoRequestBean();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        Map<String, String> params = new HashMap<>();
        params.put(CloudApiContext.BookDownLoad.CHAPTER_CONTENT_TYPE, type);
        params.put(CloudApiContext.BookDownLoad.CHAPTER_CONTENT_IDS, ids);
        String canTry = can_try ? CloudApiContext.BookDownLoad.CHAPTER_CONTENT_CAN_TRY_TRUE : CloudApiContext.BookDownLoad.CHAPTER_CONTENT_CAN_TRY_FALSE;
        params.put(CloudApiContext.BookDownLoad.CHAPTER_CONTENT_CAN_TRY, canTry);
        appBaseInfo.addRequestParams(params);
        String sign = String.format(CloudApiContext.BookShopURI.GET_CHAPTERS_CONTENT, String.valueOf(bookID));
        appBaseInfo.setSign(appBaseInfo.getSignValue(sign));
        baseRequestBean.setBaseInfo(appBaseInfo);
        baseRequestBean.bookId = bookID;
        baseRequestBean.bookName = bookName;
        baseRequestBean.localPath = localPath;
        baseRequestBean.withCookie = JDReadApplication.getInstance().getLogin();
        final RxRequestGetChaptersContent rq = new RxRequestGetChaptersContent();
        rq.setRequestBean(baseRequestBean);
        rq.execute(new RxCallback<RxRequestGetChaptersContent>() {

            @Override
            public void onSubscribe() {
                super.onSubscribe();
                showLoadingDialog(shopDataBundle, R.string.loading);
            }

            @Override
            public void onFinally() {
                super.onFinally();
                hideLoadingDialog(shopDataBundle);
            }

            @Override
            public void onNext(RxRequestGetChaptersContent request) {
                resultBean = request.getResultBean();
                invokeNext(rxCallback, GetChaptersContentAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), shopDataBundle.getEventBus());
                invokeError(rxCallback, throwable);
            }

            @Override
            public void onComplete() {
                super.onComplete();
                invokeComplete(rxCallback);
            }
        });
    }
}
