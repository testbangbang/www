package com.onyx.android.sdk.data;

import android.content.Context;
import android.graphics.Bitmap;

import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 21/11/2016.
 */

public class ThumbnailHelper {

    private DataManagerHelper parent;

    public ThumbnailHelper(final DataManagerHelper helper) {
        parent = helper;
    }

    public final DataManagerHelper getParent() {
        return parent;
    }

    private DataProviderBase getDataProvider() {
        return getParent().getDataProvider();
    }

    public Thumbnail loadThumbnail(Context context, String path, String md5, OnyxThumbnail.ThumbnailKind kind) {
        if (StringUtils.isNullOrEmpty(md5)) {
            try {
                md5 = FileUtils.computeMD5(new File(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getDataProvider().loadThumbnail(context, md5, kind);
    }

    public Bitmap loadThumbnailBitmap(Context context, Thumbnail thumbnail) {
        return getDataProvider().loadThumbnailBitmap(context, thumbnail);
    }

    public List<Bitmap> loadThumbnailBitmapList(Context context, final List<File> fileList, int limit, OnyxThumbnail.ThumbnailKind kind) {
        List<Bitmap> thumbnailList = new ArrayList<>();
        Bitmap bitmap = null;
        int thumbCount = 0;
        for (File file : fileList) {
            if (file.isDirectory()) {
                continue;
            }
            Thumbnail thumbnail = loadThumbnail(context, file.getAbsolutePath(), null, kind);
            if (thumbCount++ < Math.min(limit, fileList.size())) {
                bitmap = loadThumbnailBitmap(context, thumbnail);
            }
            thumbnailList.add(bitmap == null ?
                    ThumbnailUtils.loadDefaultThumbnailFromExtension(context, FileUtils.getFileExtension(file)) :
                    bitmap);
        }
        return thumbnailList;
    }


}
