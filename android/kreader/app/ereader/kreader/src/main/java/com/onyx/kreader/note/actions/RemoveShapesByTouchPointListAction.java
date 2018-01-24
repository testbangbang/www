package com.onyx.kreader.note.actions;

import android.graphics.Bitmap;
import android.view.SurfaceView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.cache.ReaderBitmapReferenceImpl;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.kreader.note.request.RemoveShapesByTouchPointListRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.ShapeRenderFinishEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 9/30/16.
 */

public class RemoveShapesByTouchPointListAction extends BaseAction {

    ReaderBitmapReferenceImpl docBitmap;
    private List<PageInfo> visiblePages;
    private TouchPointList touchPointList;
    private volatile List<Shape> stash = new ArrayList<>();
    private volatile SurfaceView surfaceView;

    public RemoveShapesByTouchPointListAction(Bitmap bitmap, final List<PageInfo> pageList, final TouchPointList list, final List<Shape> s, final SurfaceView view) {
        docBitmap = ReaderBitmapReferenceImpl.create(bitmap);
        visiblePages = pageList;
        touchPointList = list;
        stash.addAll(s);
        surfaceView = view;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final RemoveShapesByTouchPointListRequest request = new RemoveShapesByTouchPointListRequest(docBitmap, visiblePages, touchPointList, stash, surfaceView);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                docBitmap.close();
                readerDataHolder.getEventBus().post(ShapeRenderFinishEvent.shapeReadyEvent());
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
