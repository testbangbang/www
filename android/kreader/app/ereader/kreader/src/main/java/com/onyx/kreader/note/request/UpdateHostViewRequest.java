package com.onyx.kreader.note.request;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceView;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.note.NoteManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/12/16.
 */

public class UpdateHostViewRequest extends ReaderBaseNoteRequest {
    private SurfaceView surfaceView;
    private Rect visibleRect = new Rect();
    private ArrayList<RectF> excludeRectList = new ArrayList<>();
    private int orientation;

    public UpdateHostViewRequest(final SurfaceView sv, final Rect visibleDrawRect, final List<RectF> excludeRect, int orientation) {
        setPauseRawInputProcessor(false);
        surfaceView = sv;
        visibleRect.set(visibleDrawRect);
        excludeRectList.addAll(excludeRect);
        this.orientation = orientation;
    }

    public void execute(final NoteManager noteManager) throws Exception {
        noteManager.updateHostView(getContext(), surfaceView, visibleRect, excludeRectList, orientation);
    }

}
