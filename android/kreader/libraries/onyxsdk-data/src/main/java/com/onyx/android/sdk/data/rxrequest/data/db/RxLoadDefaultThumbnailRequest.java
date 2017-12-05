package com.onyx.android.sdk.data.rxrequest.data.db;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.FileModel;
import com.onyx.android.sdk.data.utils.DataModelUtil;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.dataprovider.R;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.InputStream;

/**
 * Created by jackdeng on 2017/12/5.
 */

public class RxLoadDefaultThumbnailRequest extends RxBaseDBRequest {

    private DataModel itemModel;
    private Context context;

    public RxLoadDefaultThumbnailRequest(DataManager dataManager, DataModel itemModel, Context context) {
        super(dataManager);
        this.itemModel = itemModel;
        this.context = context;
    }

    @Override
    public RxLoadDefaultThumbnailRequest call() throws Exception {
        CloseableReference<Bitmap> resultBitmap = getDefaultThumbnail(itemModel, context);
        if (itemModel != null && DataModelUtil.isBitmapValid(resultBitmap)) {
            itemModel.setCoverThumbnail(resultBitmap);
        }
        return this;
    }

    public static CloseableReference<Bitmap> getDefaultThumbnail(DataModel itemModel, Context context) {
        FileModel fileModel = itemModel.getFileModel();
        if (fileModel == null) {
            return null;
        }
        int res;
        switch (fileModel.getType()) {
            case TYPE_DIRECTORY:
                res = R.drawable.directory;
                break;
            case TYPE_GO_UP:
                res = R.drawable.directory_go_up;
                break;
            case TYPE_SHORT_CUT:
                res = R.drawable.directory_shortcut;
                break;
            case TYPE_FILE:
                res = getDrawable(fileModel.getFile());
                break;
            default:
                res = R.drawable.unknown_document;
                break;
        }

        try {
            @SuppressWarnings("ResourceType")
            InputStream inputStream = context.getResources().openRawResource(res);
            return ThumbnailUtils.decodeStream(inputStream, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getDrawable(File file) {
        Integer res = ThumbnailUtils.defaultThumbnailMapping().get(FilenameUtils.getExtension(file.getName()));
        if (res == null) {
            return ThumbnailUtils.thumbnailUnknown();
        }
        return res;
    }
}