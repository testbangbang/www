package com.onyx.android.note.handler;

import android.support.annotation.NonNull;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.action.AddShapesAction;
import com.onyx.android.note.action.RenderVarietyShapesAction;
import com.onyx.android.note.utils.DrawUtils;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.event.RawDrawingRenderEnabledEvent;
import com.onyx.android.sdk.pen.data.TouchPoint;
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
 * Created by lxm on 2018/2/25.
 */

public class NormalShapeHandler extends BaseHandler {

    private static final String TAG = "NormalShapeHandler";

    private Shape renderShape;
    private Disposable disposable;
    private List<Disposable> actionDisposables = new ArrayList<>();
    private ObservableEmitter<TouchPoint> drawEmitter;
    private TouchPoint downPoint;

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

    public NormalShapeHandler(@NonNull EventBus eventBus, NoteManager noteManager) {
        super(eventBus, noteManager);
    }

    private Shape createShape(TouchPoint downPoint) {
        NoteDrawingArgs drawingArgs = NoteDataBundle.getInstance().getDrawingArgs();
        Shape shape = DrawUtils.createShape(drawingArgs, ShapeFactory.POSITION_FREE);
        shape.onDown(downPoint, downPoint);
        return shape;
    }

    @Override
    public void onBeginRawDraw(boolean shortcutDrawing, final TouchPoint point) {
        super.onBeginRawDraw(shortcutDrawing, point);
        downPoint = point;
        renderShape = createShape(downPoint);
        disposable = Observable.create(new ObservableOnSubscribe<TouchPoint>() {

            @Override
            public void subscribe(ObservableEmitter<TouchPoint> e) throws Exception {
                drawEmitter = e;
                drawEmitter.onNext(point);
            }

        })
                .buffer(10)
                .observeOn(SingleThreadScheduler.scheduler())
                .subscribeOn(SingleThreadScheduler.scheduler())
                .subscribe(new Consumer<List<TouchPoint>>() {
                    @Override
                    public void accept(List<TouchPoint> touchPoints) throws Exception {
                        Shape shape = createShape(downPoint);
                        for (TouchPoint point : touchPoints) {
                            shape.onMove(point, point);
                        }
                        renderVarietyShape(shape);
                    }
                });
    }

    @Override
    public void onRawDrawingPointsMoveReceived(TouchPoint point) {
        super.onRawDrawingPointsMoveReceived(point);
        if (drawEmitter != null) {
            drawEmitter.onNext(point);
        }

    }

    @Override
    public void onEndRawDrawing(boolean outLimitRegion, TouchPoint point) {
        super.onEndRawDrawing(outLimitRegion, point);
        if (disposable != null) {
            disposable.dispose();
        }
        drawEmitter.onNext(point);
        renderShape.onUp(point, point);
        disposeAction();
        new AddShapesAction(getNoteManager())
                .setShape(renderShape)
                .execute(null);
    }

    private void renderVarietyShape(Shape shape) {
        disposeAction();
        new RenderVarietyShapesAction(getNoteManager(), shape).execute(new RxCallback() {
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

    @Override
    public boolean useDrawErase() {
        return true;
    }
}
