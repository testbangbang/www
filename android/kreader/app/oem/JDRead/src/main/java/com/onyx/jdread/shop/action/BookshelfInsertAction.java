package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookExtraInfoBean;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.db.RxRequestBookshelfInsert;

/**
 * Created by jackdeng on 2017/12/21.
 */

public class BookshelfInsertAction extends BaseAction<ShopDataBundle> {

    private String localPath;
    private BookDetailResultBean.Detail bookDetailBean;

    public BookshelfInsertAction(BookDetailResultBean.Detail bookDetailBean, String localPath) {
        this.bookDetailBean = bookDetailBean;
        this.localPath = localPath;
    }

    @Override
    public void execute(ShopDataBundle dataBundle, final RxCallback rxCallback) {
        Metadata metadata = convertBookDetailEntityToMetadata(bookDetailBean);
        final RxRequestBookshelfInsert rq = new RxRequestBookshelfInsert(dataBundle.getDataManager(), metadata);
        RxRequestBookshelfInsert.setAppContext(JDReadApplication.getInstance());
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                if (rxCallback != null) {
                    rxCallback.onNext(BookshelfInsertAction.this);
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
        });
    }

    private Metadata convertBookDetailEntityToMetadata(BookDetailResultBean.Detail detailBean) {
        Metadata metadata = new Metadata();
        metadata.setName(detailBean.getBookName());
        metadata.setAuthors(detailBean.getAuthor());
        metadata.setPublisher(detailBean.getPublisher());
        metadata.setLanguage(detailBean.getLanguage());
        metadata.setISBN(detailBean.getIsbn());
        metadata.setDescription(detailBean.getInfo());
        metadata.setNativeAbsolutePath(localPath);
        metadata.setLocation(localPath);
        BookExtraInfoBean extraInfo = detailBean.getBookExtraInfoBean();
        if (extraInfo != null) {
            metadata.setExtraAttributes(JSONObjectParseUtils.toJson(extraInfo));
        }
        return metadata;
    }
}
