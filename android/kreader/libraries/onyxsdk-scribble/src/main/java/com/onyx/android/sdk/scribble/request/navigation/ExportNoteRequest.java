package com.onyx.android.sdk.scribble.request.navigation;

import android.graphics.Bitmap;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.utils.ExportUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;

/**
 * Created by ming on 2016/12/6.
 */

public class ExportNoteRequest extends BaseNoteRequest {

    private Bitmap bitmap;
    private String document;
    private String page;

    public ExportNoteRequest(Bitmap bitmap, String document, String page) {
        this.bitmap = bitmap;
        this.document = document;
        this.page = page;
    }

    @Override
    public void execute(NoteViewHelper helper) throws Exception {
        super.execute(helper);
        File file = new File(ExportUtils.getExportNotePath(document, page));
        FileUtils.saveBitmapToFile(bitmap, file, Bitmap.CompressFormat.PNG, 100);
    }

}
