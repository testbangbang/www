package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.db.RxRequestMetadataQuery;

/**
 * Created by jackdeng on 2017/12/22.
 */

public class MetadataQueryAction extends BaseAction<ShopDataBundle> {

    private String absolutePath;
    private Metadata metadataResult;

    public MetadataQueryAction(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    @Override
    public void execute(ShopDataBundle dataBundle, final RxCallback rxCallback) {
        RxRequestMetadataQuery rxRequestMetadataQuery = new RxRequestMetadataQuery(dataBundle.getDataManager(), absolutePath);
        rxRequestMetadataQuery.execute(new RxCallback<RxRequestMetadataQuery>() {
            @Override
            public void onNext(RxRequestMetadataQuery request) {
                metadataResult = request.getMetadataResult();
                if (rxCallback != null) {
                    rxCallback.onNext(MetadataQueryAction.this);
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

    public Metadata getMetadataResult() {
        return metadataResult;
    }
}
