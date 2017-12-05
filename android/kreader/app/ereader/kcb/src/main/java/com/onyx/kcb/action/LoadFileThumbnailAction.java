package com.onyx.kcb.action;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLoadDefaultThumbnailRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLoadFileThumbnailRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.kcb.holder.DataBundle;

/**
 * Created by jackdeng on 2017/12/1.
 */

public class LoadFileThumbnailAction extends BaseAction<DataBundle> {

    private DataModel dataModel;
    private Context context;

    public LoadFileThumbnailAction(Context context, DataModel dataModel) {
        this.context = context;
        this.dataModel = dataModel;
    }

    @Override
    public void execute(DataBundle dataBundle, RxCallback rxCallback) {
        LoadFileThumbnail(dataBundle, rxCallback);
    }

    private void LoadFileThumbnail(final DataBundle dataBundle, final RxCallback rxCallback) {
        if (dataModel != null) {
            if (dataModel.isDocument.get()) {
                RxLoadFileThumbnailRequest LoadFileThumbnailRequest = new RxLoadFileThumbnailRequest(dataBundle.getDataManager(), dataModel.absolutePath.get());
                LoadFileThumbnailRequest.execute(new RxCallback<RxLoadFileThumbnailRequest>() {
                    @Override
                    public void onNext(RxLoadFileThumbnailRequest request) {
                        CloseableReference<Bitmap> resultRefBitmap = request.getResultRefBitmap();
                        if (resultRefBitmap != null && resultRefBitmap.isValid()) {
                            dataModel.setCoverThumbnail(resultRefBitmap);
                        } else {
                            addNormalThumbnail(dataBundle);
                        }
                        if (rxCallback != null) {
                            rxCallback.onNext(request);
                        }
                    }
                });
            } else {
                addNormalThumbnail(dataBundle);
            }
        }
    }

    private void addNormalThumbnail(DataBundle dataBundle) {
        RxLoadDefaultThumbnailRequest getDefaultThumbnailRequest = new RxLoadDefaultThumbnailRequest(dataBundle.getDataManager(), dataModel, context);
        getDefaultThumbnailRequest.execute(new RxCallback<RxLoadDefaultThumbnailRequest>() {
            @Override
            public void onNext(RxLoadDefaultThumbnailRequest request) {

            }
        });
    }
}