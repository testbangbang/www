package com.onyx.kreader.note.actions;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.note.request.UpdateHostViewRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/12/16.
 */

public class UpdateHostViewAction extends BaseAction {
    private SurfaceView surfaceView;
    private boolean isUpdatingVisibleRect;
    private Rect visibleRect = new Rect();
    private boolean isUpdatingExcludeRect;
    private ArrayList<RectF> excludeRectList = new ArrayList<>();
    private int orientation;

    private UpdateHostViewAction(final SurfaceView sv, boolean isUpdatingVisibleRect, final Rect visibleDrawRect, boolean isUpdatingExcludeRect, final List<RectF> excludeRect, int orientation) {
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

    public static UpdateHostViewAction updateVisibleRegion(final SurfaceView sv, final Rect visibleDrawRect, int orientation) {
        return new UpdateHostViewAction(sv, true, visibleDrawRect, false, null, orientation);
    }

    public static UpdateHostViewAction updateVisibleRegion(final SurfaceView sv, final Rect visibleDrawRect, final List<RectF> excludeRect, int orientation) {
        return new UpdateHostViewAction(sv, true, visibleDrawRect, true, excludeRect, orientation);
    }

    public static UpdateHostViewAction updateExcludeRegion(final SurfaceView sv, final List<RectF> excludeRect, int orientation) {
        return new UpdateHostViewAction(sv, false, null, true, excludeRect, orientation);
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final UpdateHostViewRequest clearPageRequest = new UpdateHostViewRequest(surfaceView, isUpdatingVisibleRect, visibleRect, isUpdatingExcludeRect, excludeRectList, orientation);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), clearPageRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }


}
