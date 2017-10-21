package com.onyx.android.sdk.reader.host.request;

import android.graphics.Bitmap;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.utils.ExportUtils;

import java.io.File;

/**
 * Created by ming on 2016/10/28.
 */

public class ExportScribbleRequest extends BaseReaderRequest {

    private Bitmap bitmap;
    private PageInfo page;

    public ExportScribbleRequest(Bitmap bitmap, PageInfo page) {
        this.bitmap = bitmap;
        this.page = page;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        File file = new File(ExportUtils.getExportScribblePath(reader.getDocumentPath(), page.getName()));
        FileUtils.saveBitmapToFile(bitmap, file, Bitmap.CompressFormat.PNG, 100);
    }
}
