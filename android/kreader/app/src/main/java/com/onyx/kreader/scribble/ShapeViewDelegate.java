package com.onyx.kreader.scribble;

import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import com.onyx.kreader.scribble.data.RawInputReader;
import com.onyx.kreader.scribble.data.TouchPointList;
import com.onyx.kreader.scribble.shape.NormalScribbleShape;
import com.onyx.kreader.scribble.shape.Shape;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.Selector;

/**
 * Created by zhuzeng on 6/16/16.
 * View delegate, connect ShapeManager and view.
 */
public class ShapeViewDelegate {

    public static abstract class Callback {

        public abstract void beforeAddNewShape(final Shape shape);

        public abstract void afterAddNewShape(final Shape shape);

        public abstract void beforeRemoveShape(final Shape shape);

        public abstract void afterRemoveShape(final Shape shape);

        public abstract void StylusChanged(int oldState, int newState);

    }

    private ShapeManager shapeManager;
    private RawInputReader rawInputReader = new RawInputReader();
    private Callback callback;


    public void setView(final SurfaceView view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // serves as switch only.
                // detect the stylus button and others. but do not receive the data.
                return false;
            }
        });
    }

    private void init() {
        rawInputReader.setInputCallback(new RawInputReader.InputCallback() {
            @Override
            public void onBeginHandWriting() {

            }

            @Override
            public void onNewStrokeReceived(TouchPointList pointList) {
                // create shape and add to memory.
                Shape shape = new NormalScribbleShape();
                shape.addPoints(pointList);
            }

            @Override
            public void onBeginErase() {

            }

            @Override
            public void onEraseReceived(TouchPointList pointList) {

            }
        });
    }

    public void setOptions() {
    }

    public void startHandWriting() {
    }

    public void startErasing() {
    }

    public void stop() {
    }

    public void setStrokeWidth(final float width) {

    }

    public void setStrokeColor(int color) {

    }

    public void setStrokeStyle(int style) {
    }

    public boolean canUndo() {
        return false;
    }

    public boolean canRedo() {
        return false;
    }

    public boolean undo() {
        return false;
    }

    public boolean redo() {
        return false;
    }

    public void setCallback(final Callback c) {
        callback = c;
    }





}
