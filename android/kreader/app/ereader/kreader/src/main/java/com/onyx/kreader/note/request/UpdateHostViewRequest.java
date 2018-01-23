package com.onyx.kreader.note.request;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceView;

import com.onyx.kreader.note.NoteManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/12/16.
 */

public class UpdateHostViewRequest extends ReaderBaseNoteRequest {
    private SurfaceView surfaceView;
    private boolean isUpdatingVisibleRect;
    private Rect visibleRect = new Rect();
    private boolean isUpdatingExcludeRect;
    private ArrayList<RectF> excludeRectList = new ArrayList<>();

    private int orientation;

    public UpdateHostViewRequest(final SurfaceView sv, boolean isUpdatingVisibleRect, final Rect visibleDrawRect, boolean isUpdatingExcludeRect, final List<RectF> excludeRect, int orientation) {
        setPauseRawInputProcessor(false);
        surfaceView = sv;
        this.isUpdatingVisibleRect = isUpdatingVisibleRect;
        if (isUpdatingVisibleRect) {
            visibleRect.set(visibleDrawRect);
        }
        this.isUpdatingExcludeRect = isUpdatingExcludeRect;
        if (isUpdatingExcludeRect) {
            excludeRectList.addAll(excludeRect);
        }
        this.orientation = orientation;
    }

    public void execute(final NoteManager noteManager) throws Exception {
        noteManager.updateHostView(getContext(), surfaceView, isUpdatingVisibleRect, visibleRect, isUpdatingExcludeRect, excludeRectList, orientation);
    }

}
