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

    private static final String TAG = "EpdShapeHandler";
    private static long SYNC_ERASE_TOUCH_POINT_SIZE = 50;
    private TouchPointList erasePoints;
    private FlowableEmitter<TouchPoint> emitter;
    private Subscription subscription;

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
        NoteDrawingArgs drawingArgs = NoteDataBundle.getInstance().getDrawDataHolder().getDrawingArgs();
        Shape shape = DrawUtils.createShape(drawingArgs, ShapeFactory.POSITION_FREE);
        shape.addPoints(pointList);
        new AddShapesBackgroundAction(getNoteManager()).setShape(shape).execute(null);
    }

    @Override
    public void onBeginRawErasing(boolean shortcutErasing, final TouchPoint point) {
        super.onBeginRawErasing(shortcutErasing, point);
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
                    syncEraseByPoints(erasePoints);
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
    public void onRawErasingPointMove(TouchPoint touchPoint) {
        super.onRawErasingPointMove(touchPoint);
        if (emitter != null) {
            emitter.onNext(touchPoint);
        }
    }

    @Override
    public void onEndRawErasing(boolean outLimitRegion, TouchPoint point) {
        super.onEndRawErasing(outLimitRegion, point);
        emitter.onNext(point);
        subscription.cancel();
    }

    @Override
    public void onRawErasingPointsReceived(TouchPointList pointList) {
        super.onRawErasingPointsReceived(pointList);
        syncEraseByPoints(pointList);
    }

    private void syncEraseByPoints(TouchPointList touchPointList) {
        if (erasePoints == null || CollectionUtils.isNullOrEmpty(erasePoints.getPoints())) {
            return;
        }
        new EraseAction(getNoteManager(), touchPointList)
                .setFixShape(true)
                .execute(null);
    }

}
