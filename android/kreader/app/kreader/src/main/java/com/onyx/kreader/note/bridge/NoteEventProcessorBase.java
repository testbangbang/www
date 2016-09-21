package com.onyx.kreader.note.bridge;

import android.view.MotionEvent;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.kreader.note.NoteManager;

/**
 * Created by zhuzeng on 9/19/16.
 */
public class NoteEventProcessorBase {

    public static abstract class InputCallback {

        public abstract void onDrawingTouchDown(final MotionEvent motionEvent, final Shape shape);

        public abstract void onDrawingTouchMove(final MotionEvent motionEvent, final Shape shape, boolean last);

        public abstract void onDrawingTouchUp(final MotionEvent motionEvent, final Shape shape);

        public abstract void onErasingTouchDown(final MotionEvent motionEvent, final Shape shape);

        public abstract void onErasingTouchMove(final MotionEvent motionEvent, final Shape shape, boolean last);

        public abstract void onErasingTouchUp(final MotionEvent motionEvent, final Shape shape);

        public abstract void onDFBShapeFinished(final Shape shape);

    }

    private NoteManager noteManager;
    private PageInfo lastPageInfo;

    public NoteEventProcessorBase(final NoteManager p) {
        noteManager = p;
    }

    public final NoteManager getNoteManager() {
        return noteManager;
    }

    public final InputCallback getCallback() {
        return getNoteManager().getInputCallback();
    }

    public final PageInfo hitTest(final float x, final float y) {
        lastPageInfo = getNoteManager().hitTest(x, y);
        return lastPageInfo;
    }

    public final PageInfo getLastPageInfo() {
        return lastPageInfo;
    }

    public boolean inLastPage(final float x, final float y) {
        if (lastPageInfo != null && lastPageInfo.getDisplayRect().contains(x, y)) {
            return true;
        }
        return false;
    }

    public void resetLastPageInfo() {
        lastPageInfo = null;
    }
}
