package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import android.graphics.Bitmap;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.utils.ExportUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;

/**
 * Created by solskjaer49 on 17/3/10 15:06.
 */

public class ExportEditedPicRequest extends AsyncBaseNoteRequest {

    private Bitmap bitmap;
    private String document;

    public ExportEditedPicRequest(Bitmap bitmap, String document) {
        this.bitmap = bitmap;
        this.document = document;
    }

    @Override
    public void execute(NoteViewHelper helper) throws Exception {
        super.execute(helper);
        File file = new File(ExportUtils.getExportPicPath(document));
        FileUtils.saveBitmapToFile(bitmap, file, Bitmap.CompressFormat.PNG, 100);
    }

}
