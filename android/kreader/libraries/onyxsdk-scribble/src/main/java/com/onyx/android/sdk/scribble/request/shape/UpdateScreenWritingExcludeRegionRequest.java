package com.onyx.android.sdk.scribble.request.shape;

import android.graphics.Rect;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by solskjaer49 on 2018/1/10 11:56.
 */

public class UpdateScreenWritingExcludeRegionRequest extends BaseNoteRequest {
    private Rect excludeRegion;

    public UpdateScreenWritingExcludeRegionRequest(Rect excludeRegion) {
        this.excludeRegion = excludeRegion;
        setPauseInputProcessor(false);
        setResumeInputProcessor(false);
    }

    @Override
    public void execute(NoteViewHelper helper) throws Exception {
        Rect[] excludeRects = new Rect[1];
        excludeRects[0] = excludeRegion;
        EpdController.setScreenHandWritingRegionExclude(helper.getView(), excludeRects);
    }
}
