package com.onyx.kreader.note.actions;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.note.request.ResumeDrawingRequest;
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
    private Rect visibleRect = new Rect();
    private ArrayList<RectF> excludeRectList = new ArrayList<>();
    private int orientation;

    public UpdateHostViewAction(final SurfaceView sv, final Rect visibleDrawRect, final List<RectF> excludeRect, int orientation) {
        surfaceView = sv;
        visibleRect.set(visibleDrawRect);
        excludeRectList.addAll(excludeRect);
        this.orientation = orientation;
    }


    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final UpdateHostViewRequest clearPageRequest = new UpdateHostViewRequest(surfaceView, visibleRect, excludeRectList, orientation);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), clearPageRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }


}
