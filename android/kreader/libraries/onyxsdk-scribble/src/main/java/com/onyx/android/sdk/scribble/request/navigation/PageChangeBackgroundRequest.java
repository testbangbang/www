package com.onyx.android.sdk.scribble.request.navigation;

import android.util.Log;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.List;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class PageChangeBackgroundRequest extends BaseNoteRequest {

    private volatile int background;


    public PageChangeBackgroundRequest(final int bk) {
        background = bk;
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
    }

    public void execute(final NoteViewHelper helper) throws Exception {
        helper.getNoteDocument().setBackground(background);
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }

}
