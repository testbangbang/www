package com.onyx.android.note.handler;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.event.BeginRawDrawEvent;
import com.onyx.android.sdk.note.event.BeginRawErasingEvent;
import com.onyx.android.sdk.note.event.EndRawErasingEvent;
import com.onyx.android.sdk.note.event.RawDrawingPointsReceivedEvent;
import com.onyx.android.sdk.note.event.RawErasingPointMoveEvent;
import com.onyx.android.sdk.note.event.RawErasingPointsReceived;
import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.pen.data.TouchPointList;
import com.onyx.android.sdk.scribble.data.NoteDocument;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by lxm on 2018/2/2.
 */

public class BaseHandler {

    private EventBus eventBus;
    private NoteManager noteManager;

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
    public void beginRawDraw(BeginRawDrawEvent event) {
        onBeginRawDraw(event.shortcutDrawing, event.point);
    }

    @Subscribe
    public void rawDrawingPointsReceived(RawDrawingPointsReceivedEvent event) {
        onRawDrawingPointsReceived(event.touchPointList);
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


    protected Shape createNewShape(int layoutType) {
        NoteDrawingArgs drawingArgs = NoteDataBundle.getInstance().getDrawDataHolder().getDrawingArgs();
        Shape shape = ShapeFactory.createShape(drawingArgs.getCurrentShapeType());
        shape.setStrokeWidth(drawingArgs.strokeWidth);
        shape.setColor(drawingArgs.getStrokeColor());
        shape.setLayoutType(layoutType);
        return shape;
    }

    public void onBeginRawDraw(boolean shortcutDrawing, TouchPoint point) {}

    public void onRawDrawingPointsReceived(TouchPointList pointList) {}

    public void onBeginRawErasing(boolean shortcutErasing, TouchPoint point) {}

    public void onRawErasingPointMove(TouchPoint touchPoint) {}

    public void onEndRawErasing(boolean outLimitRegion, TouchPoint point) {}

    public void onRawErasingPointsReceived(TouchPointList pointList) {}
}
