package com.onyx.kreader.note.bridge;

import android.graphics.Rect;
import android.graphics.RectF;
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

        public abstract void onErasingTouchDown(final MotionEvent motionEvent, final TouchPointList list);

        public abstract void onErasingTouchMove(final MotionEvent motionEvent, final TouchPointList list, boolean last);

        public abstract void onErasingTouchUp(final MotionEvent motionEvent, final TouchPointList list);

        public abstract void onRawErasingStart();

        public abstract void onRawErasingFinished(final TouchPointList list);

        public abstract void onDFBShapeStart(boolean triggerByButton);

        public abstract void onDFBShapeFinished(final Shape shape, boolean triggerByButton);

        public abstract boolean enableShortcutDrawing();

        public abstract boolean enableShortcutErasing();

        public abstract boolean enableRawEventProcessor();
    }

    private NoteManager noteManager;
    private PageInfo lastPageInfo;
    private volatile RectF limitRect = new RectF();
    private volatile RectF excludeRect = new RectF();

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

    public void setLimitRect(final Rect rect) {
        limitRect.set(rect);
    }

    public void setExcludeRect(Rect rect) {
        excludeRect.set(rect);
    }

    public boolean inLimitRect(final float x, final float y) {
        return limitRect.contains(x, y);
    }

    public boolean inExcludeRect(final float x, final float y) {
        return excludeRect.contains(x, y);
    }

    public boolean isInValidRegion(final float x, final float y) {
        return inLastPage(x, y) && inLimitRect(x, y) && !inExcludeRect(x, y);
    }
}
