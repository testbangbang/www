package com.onyx.android.note.handler;

import android.support.annotation.NonNull;

import com.onyx.android.note.action.EraseAction;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.event.RawDrawingRenderEnabledEvent;
import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.pen.data.TouchPointList;
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
 * Created by lxm on 2018/3/5.
 */

public class BaseEraseHandler extends BaseHandler {

    private static long SYNC_ERASE_TOUCH_POINT_SIZE = 50;
    private TouchPointList erasePoints;
    private FlowableEmitter<TouchPoint> emitter;
    private Subscription subscription;

    public BaseEraseHandler(@NonNull EventBus eventBus, NoteManager noteManager) {
        super(eventBus, noteManager);
    }

    @Override
    public void onActivate() {
        super.onActivate();
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
    }

    @Override
    public void onBeginRawErasing(boolean shortcutErasing, final TouchPoint point) {
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
                            eraseByPoints(erasePoints);
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
        if (emitter != null) {
            emitter.onNext(touchPoint);
        }
    }

    @Override
    public void onRawErasingPointsReceived(TouchPointList pointList) {
        eraseByPoints(pointList);
    }

    private void eraseByPoints(TouchPointList touchPointList) {
        if (erasePoints == null || CollectionUtils.isNullOrEmpty(erasePoints.getPoints())) {
            return;
        }
        new EraseAction(getNoteManager(), touchPointList)
                .setFixShape(true)
                .execute(null);
    }

    @Override
    public void onEndRawErasing(boolean outLimitRegion, TouchPoint point) {
        super.onEndRawDrawing(outLimitRegion, point);
        emitter.onNext(point);
        subscription.cancel();
    }

}
