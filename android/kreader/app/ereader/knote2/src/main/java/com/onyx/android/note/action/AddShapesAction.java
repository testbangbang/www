package com.onyx.android.note.action;

import android.support.annotation.NonNull;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.note.event.AddShapesEvent;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.AddShapesBackgroundRequest;
import com.onyx.android.sdk.note.request.AddShapesRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2018/2/25.
 */

public class AddShapesAction extends BaseNoteAction {

    private List<Shape> shapes;

    public AddShapesAction(NoteManager noteManager) {
        super(noteManager);
    }

    public AddShapesAction setShapes(List<Shape> shapes) {
        this.shapes = shapes;
        return this;
    }

    public AddShapesAction setShape(Shape shape) {
        if (CollectionUtils.isNullOrEmpty(shapes)) {
            shapes = new ArrayList<>();
        }
        shapes.add(shape);
        return this;
    }

    @Override
    public void execute(final RxCallback rxCallback) {
        AddShapesRequest request = new AddShapesRequest(getNoteManager(), shapes);
        getNoteManager().getRxManager().enqueue(request, new RxCallback<AddShapesRequest>() {
            @Override
            public void onNext(@NonNull AddShapesRequest addShapesRequest) {
                RxCallback.onNext(rxCallback, addShapesRequest);
                getNoteManager().post(new AddShapesEvent(true).setRawRenderEnable(false));
            }
        });
    }
}
