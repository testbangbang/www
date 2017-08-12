package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import android.graphics.Bitmap;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.utils.ExportUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;

/**
 * Created by ming on 2016/12/6.
 */

public class ExportNoteRequest extends AsyncBaseNoteRequest {

    private Bitmap bitmap;
    private String document;
    private String page;

    public ExportNoteRequest(Bitmap bitmap, String document, String page) {
        this.bitmap = bitmap;
        this.document = document;
        this.page = page;
    }

    @Override
    public void execute(NoteManager parent) throws Exception {
        File file = new File(ExportUtils.getExportNotePath(document, page));
        FileUtils.saveBitmapToFile(bitmap, file, Bitmap.CompressFormat.PNG, 100);
    }

}
