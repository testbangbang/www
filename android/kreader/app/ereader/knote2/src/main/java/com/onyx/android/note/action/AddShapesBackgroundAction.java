package com.onyx.android.note.action;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.AddShapesBackgroundRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2018/2/25.
 */

public class AddShapesBackgroundAction extends BaseNoteAction {

    private List<Shape> shapes;

    public AddShapesBackgroundAction(NoteManager noteManager) {
        super(noteManager);
    }

    public AddShapesBackgroundAction setShapes(List<Shape> shapes) {
        this.shapes = shapes;
        return this;
    }

    public AddShapesBackgroundAction setShape(Shape shape) {
        if (CollectionUtils.isNullOrEmpty(shapes)) {
            shapes = new ArrayList<>();
        }
        shapes.add(shape);
        return this;
    }

    @Override
    public void execute(RxCallback rxCallback) {
        AddShapesBackgroundRequest request = new AddShapesBackgroundRequest(getNoteManager(), shapes);
        request.setPauseRawDrawingRender(false);
        getNoteManager().getRxManager().enqueue(request, rxCallback);
    }
}
