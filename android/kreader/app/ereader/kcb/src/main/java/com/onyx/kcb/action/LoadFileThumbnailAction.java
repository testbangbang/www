package com.onyx.kcb.action;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.FileModel;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLoadFileThumbnailRequest;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.kcb.R;
import com.onyx.kcb.holder.DataBundle;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.InputStream;

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

    private void LoadFileThumbnail(DataBundle dataBundle, final RxCallback rxCallback) {
        if (dataModel != null) {
            if (dataModel.isDocument.get()){
                RxLoadFileThumbnailRequest LoadFileThumbnailRequest = new RxLoadFileThumbnailRequest(dataBundle.getDataManager(), dataModel.absolutePath.get());
                LoadFileThumbnailRequest.execute(new RxCallback<RxLoadFileThumbnailRequest>() {
                    @Override
                    public void onNext(RxLoadFileThumbnailRequest request) {
                        CloseableReference<Bitmap> resultRefBitmap = request.getResultRefBitmap();
                        if (resultRefBitmap != null && resultRefBitmap.isValid()) {
                            dataModel.setCoverThumbnail(resultRefBitmap);
                        } else {
                            addNormalThumbnail(dataModel);
                        }
                        if (rxCallback != null) {
                            rxCallback.onNext(request);
                        }
                    }
                });
            } else {
                addNormalThumbnail(dataModel);
            }
        }
    }

    private void addNormalThumbnail(DataModel itemModel) {
        FileModel fileModel = itemModel.getFileModel();
        if (fileModel == null) {
            return;
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
            CloseableReference<Bitmap> bitmapCloseableReference = ThumbnailUtils.decodeStream(inputStream, null);
            if (bitmapCloseableReference != null && bitmapCloseableReference.isValid()){
                itemModel.setCoverThumbnail(bitmapCloseableReference);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getDrawable(File file) {
        Integer res = ThumbnailUtils.defaultThumbnailMapping().get(FilenameUtils.getExtension(file.getName()));
        if (res == null) {
            return ThumbnailUtils.thumbnailUnknown();
        }
        return res;
    }
}