package com.onyx.android.note.handler;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.onyx.android.note.action.EraseAction;
import com.onyx.android.note.action.RefreshDrawScreenAction;
import com.onyx.android.note.event.ResizeViewEvent;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.event.BeginRawDrawEvent;
import com.onyx.android.sdk.note.event.BeginRawErasingEvent;
import com.onyx.android.sdk.note.event.EndRawDrawingEvent;
import com.onyx.android.sdk.note.event.EndRawErasingEvent;
import com.onyx.android.sdk.note.event.RawDrawingPointsMoveReceivedEvent;
import com.onyx.android.sdk.note.event.RawDrawingPointsReceivedEvent;
import com.onyx.android.sdk.note.event.RawErasingPointMoveEvent;
import com.onyx.android.sdk.note.event.RawErasingPointsReceived;
import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.pen.data.TouchPointList;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.rx.SingleThreadScheduler;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.utils.CollectionUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.schedulers.SingleScheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lxm on 2018/2/2.
 */

public class BaseHandler {

    private EventBus eventBus;
    private NoteManager noteManager;

    //erase
    private Disposable eraseDisposable;
    private List<Disposable> actionDisposables = new ArrayList<>();
    private ObservableEmitter<TouchPoint> eraseEmitter;

    public BaseHandler(@NonNull EventBus eventBus, NoteManager noteManager) {
        this.eventBus = eventBus;
        this.noteManager = noteManager;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public NoteManager getNoteManager() {
        return noteManager;
    }

    @CallSuper
    public void onActivate(){
        getEventBus().register(this);
    }

    @CallSuper
    public void onDeactivate(){
        getEventBus().unregister(this);
    }

    @Subscribe
    public void resizeView(ResizeViewEvent event) {
        onResizeView();
    }

    @Subscribe
    public void beginRawDraw(BeginRawDrawEvent event) {
        onBeginRawDraw(event.shortcutDrawing, event.point);
    }

    @Subscribe
    public void rawDrawingPointsReceived(RawDrawingPointsReceivedEvent event) {
        onRawDrawingPointsReceived(event.touchPointList);
    }

    @Subscribe
    public void rawDrawingPointsMoveReceived(RawDrawingPointsMoveReceivedEvent event) {
        onRawDrawingPointsMoveReceived(event.touchPoint);
    }

    @Subscribe
    public void endRawDrawing(EndRawDrawingEvent event) {
        onEndRawDrawing(event.outLimitRegion, event.point);
    }

    @Subscribe
    public void beginRawErasing(BeginRawErasingEvent event) {
        onBeginRawErasing(event.shortcutErasing, event.point);
    }

    @Subscribe
    public void rawErasingPointMove(RawErasingPointMoveEvent event) {
        onRawErasingPointMove(event.touchPoint);
    }

    @Subscribe
    public void endRawErasing(EndRawErasingEvent event) {
        onEndRawErasing(event.outLimitRegion, event.point);
    }

    @Subscribe
    public void rawErasingPointsReceived(RawErasingPointsReceived event) {
        onRawErasingPointsReceived(event.touchPointList);
    }

    public boolean useDrawErase() {
        return false;
    }

    private void beginDrawErasing(final TouchPoint point) {
        if (!useDrawErase()) {
            return;
        }
        eraseDisposable = Observable.create(new ObservableOnSubscribe<TouchPoint>() {

            @Override
            public void subscribe(ObservableEmitter<TouchPoint> e) throws Exception {
                eraseEmitter = e;
                eraseEmitter.onNext(point);
            }
        })
                .buffer(50)
                .subscribeOn(SingleThreadScheduler.scheduler())
                .subscribe(new Consumer<List<TouchPoint>>() {
                    @Override
                    public void accept(List<TouchPoint> touchPoints) throws Exception {
                        TouchPointList pointList = new TouchPointList();
                        for (TouchPoint touchPoint : touchPoints) {
                            pointList.add(touchPoint);
                        }
                        eraseByPoints(pointList, false);
                    }
                });
    }

    private void rawErasingPointMove(TouchPoint point) {
        if (!useDrawErase()) {
            return;
        }
        if (eraseEmitter != null) {
            eraseEmitter.onNext(point);
        }
    }

    private void endRawErasing(TouchPoint point) {
        if (!useDrawErase()) {
            return;
        }
        if (eraseDisposable != null) {
            eraseDisposable.dispose();
        }
    }

    private void rawErasingPointsReceived(TouchPointList pointList) {
        eraseByPoints(pointList, true);
    }

    private void eraseByPoints(TouchPointList touchPointList, boolean disposeAction) {
        if (touchPointList.size() == 0) {
            return;
        }
        if (disposeAction) {
            disposeAction();
        }
        new EraseAction(getNoteManager(), touchPointList)
                .setFixShape(true)
                .execute(new RxCallback() {
                    @Override
                    public void onNext(@NonNull Object o) {

                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        actionDisposables.add(d);
                    }
                });
    }

    private void disposeAction() {
        for (Disposable d : actionDisposables) {
            d.dispose();
        }
        actionDisposables = new ArrayList<>();
    }

    public void onResizeView() {
        RenderContext renderContext = getNoteManager().getRenderContext();
        renderContext.clearBgBitmapCache();
        new RefreshDrawScreenAction(getNoteManager()).execute(null);
    }

    public void onBeginRawDraw(boolean shortcutDrawing, TouchPoint point) {}

    public void onRawDrawingPointsReceived(TouchPointList pointList) {}

    public void onRawDrawingPointsMoveReceived(TouchPoint point) {}

    public void onEndRawDrawing(boolean outLimitRegion, TouchPoint point) {}

    public void onBeginRawErasing(boolean shortcutErasing, TouchPoint point) {
        beginDrawErasing(point);
    }

    public void onRawErasingPointMove(TouchPoint touchPoint) {
        rawErasingPointMove(touchPoint);
    }

    public void onEndRawErasing(boolean outLimitRegion, TouchPoint point) {
        endRawErasing(point);
    }

    public void onRawErasingPointsReceived(TouchPointList pointList) {
        rawErasingPointsReceived(pointList);
    }
}
