package com.onyx.kreader.note.bridge;

import android.view.MotionEvent;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;

/**
 * Created by zhuzeng on 9/19/16.
 */
public class NoteEventProcessorBase {

    public static abstract class InputCallback {

        // when received pen down or stylus button
        public abstract void onBeginRawData();

        // when pen released.
        public abstract void onRawTouchPointListReceived(final Shape shape, final TouchPointList pointList);

        public abstract void onDrawingTouchDown(final MotionEvent motionEvent, final Shape shape);

        public abstract void onDrawingTouchMove(final MotionEvent motionEvent, final Shape shape, boolean last);

        public abstract void onDrawingTouchUp(final MotionEvent motionEvent, final Shape shape);

        // caller should render the page here.
        public abstract void onBeginErasing();

        // caller should draw erase indicator
        public abstract void onErasing(final MotionEvent motionEvent);

        // caller should do hit test in current page, remove shapes hit-tested.
        public abstract void onEraseTouchPointListReceived(final TouchPointList pointList);

    }



}
