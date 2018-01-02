package com.onyx.jdread.shop.action;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.AddBookToSmoothCardBookBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestAddBookToSmoothCard;
import com.onyx.jdread.shop.utils.BookDownloadUtils;

/**
 * Created by jackdeng on 2017/12/29.
 */

public class AddBookToSmoothCardAction extends BaseAction {


    private boolean shouldDownLoad;
    private BookDetailResultBean.Detail bookDetailBean;
    private AddBookToSmoothCardBookBean addBookToSmoothCardResultBean;

    public AddBookToSmoothCardAction(BookDetailResultBean.Detail bookDetailBean, boolean shouldDownLoad) {
        this.bookDetailBean = bookDetailBean;
        this.shouldDownLoad = shouldDownLoad;
    }

    @Override
    public void execute(final ShopDataBundle dataBundle, RxCallback rxCallback) {
        BaseRequestBean requestBean = new BaseRequestBean();
        requestBean.setAppBaseInfo(dataBundle.getAppBaseInfo());
        JSONObject body = new JSONObject();
        body.put(CloudApiContext.AddToSmooth.EBOOK_ID, bookDetailBean.getEbookId());
        requestBean.setBody(body.toJSONString());
        RxRequestAddBookToSmoothCard rq = new RxRequestAddBookToSmoothCard(bookDetailBean);
        rq.setRequestBean(requestBean);
        rq.execute(new RxCallback<RxRequestAddBookToSmoothCard>() {
            @Override
            public void onNext(RxRequestAddBookToSmoothCard rq) {
                addBookToSmoothCardResultBean = rq.getAddBookToSmoothCardBookBean();
                if (addBookToSmoothCardResultBean != null && !StringUtils.isNullOrEmpty(addBookToSmoothCardResultBean.cardNO)) {
                    BookDownloadUtils.download(bookDetailBean, dataBundle);
                } else {
                    ToastUtil.showToast(JDReadApplication.getInstance(), JDReadApplication.getInstance().getString(R.string.add_to_smooth_list_failed));
                }
                if (shouldDownLoad) {
                    bookDetailBean.setFluentRead(true);
                    BookDownloadUtils.download(bookDetailBean, dataBundle);
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
                if (addBookToSmoothCardResultBean != null) {
                    if (Constants.CODE_STATE_THREE.equals(addBookToSmoothCardResultBean.code) || Constants.CODE_STATE_FOUR.equals(addBookToSmoothCardResultBean.code)) {
                        JDReadApplication.getInstance().setLogin(false);
                        //TODO autoLogin();
                    }
                }
            }
        });
    }
}