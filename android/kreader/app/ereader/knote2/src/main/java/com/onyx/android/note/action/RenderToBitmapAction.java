package com.onyx.android.note.action;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.BaseNoteRequest;
import com.onyx.android.sdk.note.request.RenderToBitmapRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2018/2/25.
 */

public class RenderToBitmapAction extends BaseNoteAction {

    private List<Shape> shapes;
    private boolean pauseRawDrawRender;
    private boolean renderToScreen;

    public RenderToBitmapAction(NoteManager noteManager) {
        super(noteManager);
    }

    public RenderToBitmapAction setShapes(List<Shape> shapes) {
        this.shapes = shapes;
        return this;
    }

    public RenderToBitmapAction setShape(Shape shape) {
        if (CollectionUtils.isNullOrEmpty(shapes)) {
            shapes = new ArrayList<>();
        }
        shapes.add(shape);
        return this;
    }

    public RenderToBitmapAction setPauseRawDrawRender(boolean pauseRawDrawRender) {
        this.pauseRawDrawRender = pauseRawDrawRender;
        return this;
    }

    public RenderToBitmapAction setRenderToScreen(boolean renderToScreen) {
        this.renderToScreen = renderToScreen;
        return this;
    }

    @Override
    public void execute(RxCallback rxCallback) {
        BaseNoteRequest request = new RenderToBitmapRequest(getNoteManager(), shapes)
                .setPauseRawDrawingRender(pauseRawDrawRender)
                .setRenderToScreen(renderToScreen);
        getNoteManager().getRxManager().enqueue(request, rxCallback);
    }
}
