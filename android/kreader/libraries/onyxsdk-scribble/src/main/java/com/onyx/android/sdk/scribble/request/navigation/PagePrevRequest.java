package com.onyx.android.sdk.scribble.request.navigation;

import android.util.Log;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PagePrevRequest extends BaseNoteRequest {

    public PagePrevRequest() {
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
    }

    public void execute(final NoteViewHelper helper) throws Exception {
        long start = System.currentTimeMillis();
        helper.getNoteDocument().prevPage();
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
        long end = System.currentTimeMillis();
        Log.e("######", "render takes: " + (end - start));
    }

}
