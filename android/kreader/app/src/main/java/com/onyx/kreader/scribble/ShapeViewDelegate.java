package com.onyx.kreader.scribble;

import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
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

    public void setOptions() {
    }

    public void setState(int newState) {
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





}
