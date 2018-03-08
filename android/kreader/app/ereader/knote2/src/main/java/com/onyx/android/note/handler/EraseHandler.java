package com.onyx.android.note.handler;

import android.graphics.Color;
import android.support.annotation.NonNull;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.action.AddShapesAction;
import com.onyx.android.note.action.RenderToBitmapAction;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.data.ScribbleMode;
import com.onyx.android.sdk.note.event.RawDrawingRenderEnabledEvent;
import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.pen.data.TouchPointList;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;

import org.greenrobot.eventbus.EventBus;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lxm on 2018/3/5.
 */

public class EraseHandler extends BaseHandler {

    private static long SYNC_ERASE_TOUCH_POINT_SIZE = 50;
    private TouchPointList erasePoints;
    private FlowableEmitter<TouchPoint> emitter;
    private Subscription subscription;

    public EraseHandler(@NonNull EventBus eventBus, NoteManager noteManager) {
        super(eventBus, noteManager);
    }

    @Override
    public void onActivate() {
        super.onActivate();
        getNoteManager().post(new RawDrawingRenderEnabledEvent(false));
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        getNoteManager().post(new RawDrawingRenderEnabledEvent(true));
    }

    @Override
    public void onBeginRawDraw(boolean shortcutDrawing, final TouchPoint point) {
        super.onBeginRawDraw(shortcutDrawing, point);
        erasePoints = new TouchPointList();
        Flowable.create(new FlowableOnSubscribe<TouchPoint>() {

            @Override
            public void subscribe(FlowableEmitter<TouchPoint> e) throws Exception {
                emitter = e;
                emitter.onNext(point);
            }

        }, BackpressureStrategy.BUFFER)
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<TouchPoint>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        subscription = s;
                        subscription.request(SYNC_ERASE_TOUCH_POINT_SIZE);
                    }

                    @Override
                    public void onNext(TouchPoint touchPoint) {
                        erasePoints.add(touchPoint);
                        if (erasePoints.size() == SYNC_ERASE_TOUCH_POINT_SIZE) {
                            renderToBitmap(erasePoints);
                            erasePoints = new TouchPointList();
                            subscription.request(SYNC_ERASE_TOUCH_POINT_SIZE);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onRawDrawingPointsMoveReceived(TouchPoint point) {
        super.onRawDrawingPointsMoveReceived(point);
        if (emitter != null) {
            emitter.onNext(point);
        }
    }

    @Override
    public void onRawDrawingPointsReceived(TouchPointList pointList) {
        super.onRawDrawingPointsReceived(pointList);
        new AddShapesAction(getNoteManager())
                .setShape(createEraseShape(pointList))
                .execute(null);
    }

    @Override
    public void onEndRawDrawing(boolean outLimitRegion, TouchPoint point) {
        super.onEndRawDrawing(outLimitRegion, point);
        emitter.onNext(point);
        subscription.cancel();
    }

    private void renderToBitmap(TouchPointList touchPointList) {
        new RenderToBitmapAction(getNoteManager())
                .setShape(createEraseShape(touchPointList))
                .setPauseRawDraw(false)
                .setRenderToScreen(true)
                .execute(null);
    }

    private Shape createEraseShape(TouchPointList touchPointList) {
        NoteDrawingArgs drawingArgs = NoteDataBundle.getInstance().getDrawDataHolder().getDrawingArgs();
        Shape shape = ShapeFactory.createShape(ShapeFactory.SHAPE_ERASE_OVERLAY);
        shape.setStrokeWidth(drawingArgs.strokeWidth);
        shape.setColor(Color.TRANSPARENT);
        shape.setLayoutType(ShapeFactory.POSITION_FREE);
        shape.addPoints(touchPointList);
        return shape;
    }
}
