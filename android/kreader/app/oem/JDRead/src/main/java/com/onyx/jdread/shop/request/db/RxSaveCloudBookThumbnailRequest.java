package com.onyx.jdread.shop.request.db;

import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;

import java.io.File;

/**
 * Created by hehai on 18-1-22.
 */

public class RxSaveCloudBookThumbnailRequest extends RxBaseDBRequest {
    private Metadata metadata;

    public RxSaveCloudBookThumbnailRequest(DataManager dm, Metadata metadata) {
        super(dm);
        this.metadata = metadata;
    }

    public RxSaveCloudBookThumbnailRequest(DataManager dm) {
        super(dm);
    }

    @Override
    public RxSaveCloudBookThumbnailRequest call() throws Exception {
        try {
            Bitmap bitmap = Glide.with(JDReadApplication.getInstance()).load(metadata.getCoverUrl()).asBitmap().into(
                    ResManager.getInteger(R.integer.cloud_book_cover_width), ResManager.getInteger(R.integer.cloud_book_cover_height)).get();
            ThumbnailUtils.insertThumbnail(getAppContext(), getDataProvider(), metadata.getNativeAbsolutePath(), metadata.getAssociationId(), bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
}
