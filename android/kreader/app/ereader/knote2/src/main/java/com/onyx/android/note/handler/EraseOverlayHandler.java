package com.onyx.android.note.handler;

import android.graphics.Color;
import android.support.annotation.NonNull;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.action.AddShapesAction;
import com.onyx.android.note.action.RenderToBitmapAction;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.event.RawDrawingRenderEnabledEvent;
import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.pen.data.TouchPointList;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.rx.SingleThreadScheduler;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by lxm on 2018/3/5.
 */

public class EraseOverlayHandler extends BaseHandler {

    private Disposable disposable;
    private List<Disposable> actionDisposables = new ArrayList<>();
    private ObservableEmitter<TouchPoint> eraseOverlayEmitter;

    public EraseOverlayHandler(@NonNull EventBus eventBus, NoteManager noteManager) {
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
        disposable = Observable.create(new ObservableOnSubscribe<TouchPoint>() {

            @Override
            public void subscribe(ObservableEmitter<TouchPoint> e) throws Exception {
                eraseOverlayEmitter = e;
                eraseOverlayEmitter.onNext(point);
            }

        })
                .buffer(20)
                .observeOn(SingleThreadScheduler.scheduler())
                .subscribeOn(SingleThreadScheduler.scheduler())
                .subscribe(new Consumer<List<TouchPoint>>() {
                    @Override
                    public void accept(List<TouchPoint> touchPoints) throws Exception {
                        TouchPointList pointList = new TouchPointList();
                        for (TouchPoint point : touchPoints) {
                            pointList.add(point);
                        }
                        renderToBitmap(pointList);
                    }
                });
    }

    @Override
    public void onRawDrawingPointsMoveReceived(TouchPoint point) {
        super.onRawDrawingPointsMoveReceived(point);
        if (eraseOverlayEmitter != null) {
            eraseOverlayEmitter.onNext(point);
        }
    }

    @Override
    public void onRawDrawingPointsReceived(TouchPointList pointList) {
        super.onRawDrawingPointsReceived(pointList);
        disposeAction();
        new AddShapesAction(getNoteManager())
                .setShape(createEraseShape(pointList))
                .execute(null);
    }

    @Override
    public void onEndRawDrawing(boolean outLimitRegion, TouchPoint point) {
        super.onEndRawDrawing(outLimitRegion, point);
        eraseOverlayEmitter.onNext(point);
        disposable.dispose();
    }

    private void renderToBitmap(TouchPointList touchPointList) {
        disposeAction();
        new RenderToBitmapAction(getNoteManager())
                .setShape(createEraseShape(touchPointList))
                .setPauseRawDrawRender(false)
                .setRenderToScreen(true)
                .execute(new RxCallback() {
                    @Override
                    public void onNext(@NonNull Object o) {

                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        super.onSubscribe(d);
                        actionDisposables.add(d);
                    }
                });
    }

    private Shape createEraseShape(TouchPointList touchPointList) {
        NoteDrawingArgs drawingArgs = NoteDataBundle.getInstance().getDrawingArgs();
        Shape shape = ShapeFactory.createShape(ShapeFactory.SHAPE_ERASE_OVERLAY);
        shape.setStrokeWidth(drawingArgs.strokeWidth);
        shape.setColor(Color.TRANSPARENT);
        shape.setLayoutType(ShapeFactory.LayoutType.FREE.ordinal());
        shape.addPoints(touchPointList);
        return shape;
    }

    private void disposeAction() {
        for (Disposable d : actionDisposables) {
            d.dispose();
        }
        actionDisposables = new ArrayList<>();
    }
}
