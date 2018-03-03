package com.onyx.android.note.handler;

import android.support.annotation.NonNull;

import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.pen.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2018/2/5.
 */

public class SpanTextHandler extends BaseHandler {

    private List<Shape> dirtyStash = new ArrayList<>();

    public SpanTextHandler(@NonNull EventBus eventBus, NoteManager noteManager) {
        super(eventBus, noteManager);
    }

    @Override
    public void onRawDrawingPointsReceived(TouchPointList pointList) {
        super.onRawDrawingPointsReceived(pointList);
        Shape shape = createNewShape(ShapeFactory.POSITION_LINE_LAYOUT);
        dirtyStash.add(shape);
    }

    public List<Shape> detachStash() {
        final List<Shape> temp = new ArrayList<>();
        temp.addAll(dirtyStash);
        dirtyStash = new ArrayList<>();
        return temp;
    }
}
