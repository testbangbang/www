package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
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
        showLoadingDialog(dataBundle, R.string.loading);
        RxRequestMetadataQuery rxRequestMetadataQuery = new RxRequestMetadataQuery(dataBundle.getDataManager(), bookId);
        rxRequestMetadataQuery.execute(new RxCallback<RxRequestMetadataQuery>() {
            @Override
            public void onNext(RxRequestMetadataQuery request) {
                metadataResult = request.getMetadataResult();
                hideLoadingDialog(dataBundle);
                if (rxCallback != null) {
                    rxCallback.onNext(MetadataQueryAction.this);
                    rxCallback.onComplete();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                hideLoadingDialog(dataBundle);
                if (rxCallback != null) {
                    rxCallback.onError(throwable);
                }
            }
        });
    }

    public Metadata getMetadataResult() {
        return metadataResult;
    }
}