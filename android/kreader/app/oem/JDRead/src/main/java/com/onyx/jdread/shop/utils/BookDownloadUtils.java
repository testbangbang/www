package com.onyx.jdread.shop.utils;

import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.personal.action.BookDownloadUrlAction;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookExtraInfoBean;
import com.onyx.jdread.shop.model.ShopDataBundle;

/**
 * Created by hehai on 17-4-27.
 */

public class BookDownloadUtils {
    private static final String TAG = BookDownloadUtils.class.getSimpleName();

    public static void download(final BookDetailResultBean.Detail entity, ShopDataBundle dataBundle) {
        if (StringUtils.isNullOrEmpty(entity.getDownLoadUrl())) {
            entity.getBookExtraInfoBean().isWholeBook = true;
            BookDownloadUrlAction downloadUrlAction = new BookDownloadUrlAction(entity);
            downloadUrlAction.execute(dataBundle, new RxCallback() {
                @Override
                public void onNext(Object o) {

                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                }
            });
        } else {
            BookExtraInfoBean bookExtraInfoBean = entity.getBookExtraInfoBean();
            if (bookExtraInfoBean != null && bookExtraInfoBean.downLoadState != 0 && DownLoadHelper.isDownloading(bookExtraInfoBean.downLoadState)) {
                OnyxDownloadManager.getInstance().pauseTask(bookExtraInfoBean.downLoadTaskTag, false);
            } else {

            }
        }
    }
}
