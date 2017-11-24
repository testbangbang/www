package com.onyx.kcb.action;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.rxrequest.data.db.RxExtractMetadataRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.kcb.holder.LibraryDataHolder;

/**
 * Created by hehai on 17-11-23.
 */

public class ExtractMetadataAction extends BaseAction<LibraryDataHolder> {
    private QueryArgs queryArgs;
    private boolean forceScanMetadata;

    public ExtractMetadataAction(QueryArgs queryArgs, boolean forceScanMetadata) {
        this.queryArgs = queryArgs;
        this.forceScanMetadata = forceScanMetadata;
    }

    @Override
    public void execute(LibraryDataHolder dataHolder, final RxCallback baseCallback) {
        RxExtractMetadataRequest rxThumbnailLoadRequest = new RxExtractMetadataRequest(dataHolder.getDataManager(), queryArgs);
        rxThumbnailLoadRequest.setForceScanMetadata(forceScanMetadata);
        rxThumbnailLoadRequest.execute(new RxCallback<RxExtractMetadataRequest>() {
            @Override
            public void onNext(RxExtractMetadataRequest thumbnailLoadRequest) {
                if (baseCallback != null) {
                    baseCallback.onNext(thumbnailLoadRequest);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (baseCallback != null) {
                    baseCallback.onNext(throwable);
                }
            }
        });
    }
}
