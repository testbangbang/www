package com.onyx.kreader.note.bridge;

import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.math.OnyxMatrix;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.kreader.note.data.ReaderShapeFactory;
import net.lingala.zip4j.util.Raw;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 9/18/16.
 * Receive events from touch event or input event reader and send render command to screen
 */
public class NoteEventProcessorManager {

    private Shape currentShape = null;
    private volatile View view;
    private List<Shape> dirtyStash = new ArrayList<>();
    private volatile NoteDrawingArgs noteDrawingArgs = new NoteDrawingArgs();
    private NoteEventProcessorBase.InputCallback callback;
    private TouchEventProcessor touchEventProcessor;
    private RawEventProcessor rawEventProcessor;

    public NoteDrawingArgs getNoteDrawingArgs() {
        return noteDrawingArgs;
    }

    public void setNoteDrawingArgs(NoteDrawingArgs noteDrawingArgs) {
        this.noteDrawingArgs = noteDrawingArgs;
    }

    public NoteEventProcessorBase.InputCallback getCallback() {
        return callback;
    }

    public void setCallback(NoteEventProcessorBase.InputCallback callback) {
        this.callback = callback;
    }

    public View getView() {
        return view;
    }

    public void update(final View targetView, final OnyxMatrix matrix, final Matrix digitizer) {
        view = targetView;
        getTouchEventProcessor().update(targetView, matrix);
        getRawEventProcessor().update(digitizer, null);
    }

    private TouchEventProcessor getTouchEventProcessor() {
        if (touchEventProcessor == null) {
            touchEventProcessor = new TouchEventProcessor(this);
        }
        return touchEventProcessor;
    }

    private RawEventProcessor getRawEventProcessor() {
        if (rawEventProcessor == null) {
            rawEventProcessor = new RawEventProcessor(this);
            rawEventProcessor.start();
            rawEventProcessor.pause();
        }
        return rawEventProcessor;
    }

    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return onTouchEventDrawing(motionEvent);
    }

    public boolean onTouchEventDrawing(final MotionEvent motionEvent) {
        return getTouchEventProcessor().onTouchEventDrawing(motionEvent);
    }

    public boolean onTouchEventErasing(final MotionEvent motionEvent) {
        return getTouchEventProcessor().onTouchEventErasing(motionEvent);
    }

    public void beforeDownMessage(final Shape currentShape) {
        if (ReaderShapeFactory.isDFBShape(currentShape.getType())) {
            enableScreenPost(false);
        } else {
            enableScreenPost(true);
        }
    }

    public void enableScreenPost(boolean enable) {
        if (view != null) {
            EpdController.enablePost(view, enable ? 1 : 0);
        }
    }

    public Shape createNewShape() {
        Shape shape = ShapeFactory.createShape(getNoteDrawingArgs().currentShapeType);
        shape.setStrokeWidth(getNoteDrawingArgs().strokeWidth);
        shape.setColor(getNoteDrawingArgs().strokeColor);
        currentShape = shape;
        return shape;
    }

    public Shape getCurrentShape() {
        return currentShape;
    }

    public void addToStash(final Shape shape) {
        dirtyStash.add(shape);
    }

}
