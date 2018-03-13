package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookExtraInfoBean;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.db.RxRequestBookshelfInsert;
import com.onyx.jdread.shop.request.db.RxRequestUpdateDownloadInfo;

/**
 * Created by jackdeng on 2017/12/21.
 */

public class UpdateDownloadInfoAction extends BaseAction<ShopDataBundle> {

    private BookExtraInfoBean extraInfo;

    public UpdateDownloadInfoAction(BookExtraInfoBean extraInfo) {
        this.extraInfo = extraInfo;
    }

    @Override
    public void execute(ShopDataBundle dataBundle, final RxCallback rxCallback) {
        final RxRequestUpdateDownloadInfo rq = new RxRequestUpdateDownloadInfo(dataBundle.getDataManager());
        rq.setExtraInfo(extraInfo);
        RxRequestUpdateDownloadInfo.setAppContext(JDReadApplication.getInstance());
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                if (rxCallback != null) {
                    rxCallback.onNext(UpdateDownloadInfoAction.this);
                    rxCallback.onComplete();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (rxCallback != null) {
                    rxCallback.onError(throwable);
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
                if (rxCallback != null) {
                    rxCallback.onComplete();
                }
            }
        });
    }
}
