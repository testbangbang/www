package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.db.RxRequestMetadataQuery;

/**
 * Created by jackdeng on 2017/12/22.
 */

public class MetadataQueryAction extends BaseAction<ShopDataBundle> {

    private String bookId;
    private Metadata metadataResult;

    public MetadataQueryAction(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public void execute(final ShopDataBundle dataBundle, final RxCallback rxCallback) {
        RxRequestMetadataQuery rxRequestMetadataQuery = new RxRequestMetadataQuery(dataBundle.getDataManager(), bookId);
        rxRequestMetadataQuery.execute(new RxCallback<RxRequestMetadataQuery>() {

            @Override
            public void onSubscribe() {
                super.onSubscribe();
                invokeSubscribe(rxCallback);
            }

            @Override
            public void onFinally() {
                super.onFinally();
                invokeFinally(rxCallback);
            }

            @Override
            public void onNext(RxRequestMetadataQuery request) {
                metadataResult = request.getMetadataResult();
                invokeNext(rxCallback, MetadataQueryAction.this);
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

    public Metadata getMetadataResult() {
        return metadataResult;
    }
}
