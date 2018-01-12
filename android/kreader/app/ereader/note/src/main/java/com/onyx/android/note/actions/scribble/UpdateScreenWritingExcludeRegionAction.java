package com.onyx.android.note.actions.scribble;

import android.graphics.Rect;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.request.shape.UpdateScreenWritingExcludeRegionRequest;

/**
 * Created by solskjaer49 on 2018/1/12 17:59.
 */

public class UpdateScreenWritingExcludeRegionAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {

    public UpdateScreenWritingExcludeRegionAction(Rect excludeRect, boolean resumeDrawing) {
        this.resumeDrawing = resumeDrawing;
        this.excludeRect = excludeRect;
    }

    private boolean resumeDrawing;
    private Rect excludeRect;

    @Override
    public void execute(T activity, BaseCallback callback) {
        UpdateScreenWritingExcludeRegionRequest updateExcludeRegionRequest =
                new UpdateScreenWritingExcludeRegionRequest(excludeRect, resumeDrawing);
        activity.submitRequest(updateExcludeRegionRequest, callback);
    }
}
