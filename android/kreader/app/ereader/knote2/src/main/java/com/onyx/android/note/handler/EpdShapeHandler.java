package com.onyx.android.note.handler;

import android.support.annotation.NonNull;

import com.onyx.android.note.action.RenderToBitmapAction;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.pen.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2018/2/25.
 */

public class EpdShapeHandler extends BaseHandler {

    public EpdShapeHandler(@NonNull EventBus eventBus, NoteManager noteManager) {
        super(eventBus, noteManager);
    }

    @Override
    public void onBeginRawDraw(boolean shortcutDrawing, TouchPoint point) {
        super.onBeginRawDraw(shortcutDrawing, point);
    }

    @Override
    public void onRawDrawingPointsReceived(TouchPointList pointList) {
        super.onRawDrawingPointsReceived(pointList);
        Shape shape = createNewShape(ShapeFactory.POSITION_FREE);
        shape.addPoints(pointList);
        new RenderToBitmapAction(getNoteManager()).setShape(shape).execute(null);
    }

}
