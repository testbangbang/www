package com.onyx.android.note.handler;

import android.support.annotation.NonNull;
import android.util.Log;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.action.AddShapesBackgroundAction;
import com.onyx.android.note.action.EraseAction;
import com.onyx.android.note.utils.DrawUtils;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.pen.data.TouchPointList;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.utils.CollectionUtils;

import org.greenrobot.eventbus.EventBus;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lxm on 2018/2/25.
 */

public class EpdShapeHandler extends BaseHandler {

    EpdShapeHandler(@NonNull EventBus eventBus, NoteManager noteManager) {
        super(eventBus, noteManager);
    }

    @Override
    public void onBeginRawDraw(boolean shortcutDrawing, TouchPoint point) {
        super.onBeginRawDraw(shortcutDrawing, point);
    }

    @Override
    public void onRawDrawingPointsReceived(TouchPointList pointList) {
        super.onRawDrawingPointsReceived(pointList);
        NoteDrawingArgs drawingArgs = NoteDataBundle.getInstance().getDrawingArgs();
        Shape shape = DrawUtils.createShape(drawingArgs, ShapeFactory.POSITION_FREE);
        shape.addPoints(pointList);
        new AddShapesBackgroundAction(getNoteManager()).setShape(shape).execute(null);
    }

    @Override
    public boolean useDrawErase() {
        return true;
    }
}
