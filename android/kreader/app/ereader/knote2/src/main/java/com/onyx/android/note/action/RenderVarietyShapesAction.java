package com.onyx.android.note.action;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.RenderVarietyShapesRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2018/3/9.
 */

public class RenderVarietyShapesAction extends BaseNoteAction {

    private List<Shape> shapes;

    public RenderVarietyShapesAction(NoteManager noteManager, List<Shape> shapes) {
        super(noteManager);
        this.shapes = shapes;
    }

    public RenderVarietyShapesAction(NoteManager noteManager, Shape shape) {
        super(noteManager);
        if (shapes == null) {
            shapes = new ArrayList<>();
        }
        shapes.add(shape);
    }

    @Override
    public void execute(RxCallback rxCallback) {
        RenderVarietyShapesRequest request = new RenderVarietyShapesRequest(getNoteManager(), shapes);
        request.setPauseRawDrawing(false);
        getNoteManager().getRxManager().enqueue(request, rxCallback);
    }
}
