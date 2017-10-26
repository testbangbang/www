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
    private boolean isSideNotePage;

    public ExportScribbleRequest(Bitmap bitmap, PageInfo page, boolean isSideNotePage) {
        this.bitmap = bitmap;
        this.page = page;
        this.isSideNotePage = isSideNotePage;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        String pageName = page.getName();
        if (isSideNotePage) {
            pageName = pageName + "_" + page.getSubPage();
        }
        File file = new File(ExportUtils.getExportScribblePath(reader.getDocumentPath(), pageName));
        FileUtils.saveBitmapToFile(bitmap, file, Bitmap.CompressFormat.PNG, 100);
    }
}
