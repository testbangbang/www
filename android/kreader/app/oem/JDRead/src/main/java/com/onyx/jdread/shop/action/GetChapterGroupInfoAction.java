package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.cloud.entity.GetChapterGroupInfoRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BatchDownloadResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.BookBatchDownloadViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestChapterGroupInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jackdeng on 2017/12/13.
 */

public class GetChapterGroupInfoAction extends BaseAction<ShopDataBundle> {

    private String startChapter;
    private long bookID;
    private BatchDownloadResultBean resultBean;
    private BookBatchDownloadViewModel bookBatchDownloadViewModel;

    public GetChapterGroupInfoAction(long bookID, String startChapter) {
        this.bookID = bookID;
        this.startChapter = startChapter;
    }

    public void setViewModel(BookBatchDownloadViewModel bookBatchDownloadViewModel) {
        this.bookBatchDownloadViewModel = bookBatchDownloadViewModel;
    }

    public BatchDownloadResultBean getResultBean() {
        return resultBean;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        GetChapterGroupInfoRequestBean baseRequestBean = new GetChapterGroupInfoRequestBean();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        Map<String, String> queryArgs = new HashMap<>();
        queryArgs.put(CloudApiContext.BookDownLoad.START_CHAPTER_ID, startChapter);
        appBaseInfo.addRequestParams(queryArgs);
        String sign = String.format(CloudApiContext.BookShopURI.GET_CHAPTER_GROUP_INFO, String.valueOf(bookID));
        appBaseInfo.setSign(appBaseInfo.getSignValue(sign));
        baseRequestBean.setBaseInfo(appBaseInfo);
        baseRequestBean.bookId = bookID;
        final RxRequestChapterGroupInfo rq = new RxRequestChapterGroupInfo();
        rq.setRequestBean(baseRequestBean);
        rq.execute(new RxCallback<RxRequestChapterGroupInfo>() {

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
            public void onNext(RxRequestChapterGroupInfo request) {
                resultBean = request.getResultBean();
                handlerResult();
                invokeNext(rxCallback, GetChapterGroupInfoAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                invokeError(rxCallback, throwable);
            }

            @Override
            public void onComplete() {
                super.onComplete();
                invokeComplete(rxCallback);
            }
        });
    }

    private void handlerResult() {
        if (resultBean != null && resultBean.isSucceed()) {
            BatchDownloadResultBean.DataBean data = resultBean.data;
            if (data != null && data.list != null) {
                for (int i = 0; i < data.list.size(); i++) {
                    BatchDownloadResultBean.DataBean.ListBean listBean = data.list.get(i);
                    if (i == 0) {
                        listBean.chapterCount = ResManager.getString(R.string.book_batch_download_item_current_chapter);
                    } else {
                        listBean.chapterCount = String.format(ResManager.getString(R.string.book_batch_download_item_after_chapter), listBean.count);
                    }
                }
            }
            bookBatchDownloadViewModel.setDataBean(data);
        }
    }
}
