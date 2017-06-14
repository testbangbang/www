package com.onyx.edu.reader.ui.handler;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.edu.reader.note.actions.ResumeDrawingAction;
import com.onyx.edu.reader.note.actions.StopNoteActionChain;
import com.onyx.edu.reader.note.request.StartNoteRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/6/9.
 */

public class FormScribbleHandler extends FormFieldHandler {

    public FormScribbleHandler(HandlerManager parent) {
        super(parent);
    }

    public boolean onScroll(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public boolean onSingleTapUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return readerDataHolder.getNoteManager().inScribbleRect(TouchPoint.create(e));
    }

    public boolean onScaleEnd(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        return false;
    }

    public boolean onScaleBegin(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        return false;
    }

    public boolean onScale(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector)  {
        return false;
    }

    public boolean onActionUp(ReaderDataHolder readerDataHolder, final float startX, final float startY, final float endX, final float endY) {
        return false;
    }

    @Override
    public boolean onKeyDown(ReaderDataHolder readerDataHolder, int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                return false;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return false;
            case KeyEvent.KEYCODE_DPAD_UP:
                return false;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return false;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return false;
            case KeyEvent.KEYCODE_MENU:
                return true;
            case KEYCDOE_ERASE:
            case KEYCDOE_ERASE_KK:
            case KeyEvent.KEYCODE_ALT_LEFT:
            case KeyEvent.KEYCODE_ALT_RIGHT:
                if (isEnableBigPen()) {
                    getParent().setActiveProvider(HandlerManager.ERASER_PROVIDER);
                }
                return false;
            case KEYCDOE_SCRIBE:
            case KEYCDOE_SCRIBE_KK:
                return false;
            default:
                return super.onKeyDown(readerDataHolder,keyCode,event);
        }
    }

    private boolean isEnableBigPen() {
        return true;
    }

    @Override
    public boolean onTouchEvent(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (e.getPointerCount() > 1) {
            return false;
        }
        return readerDataHolder.getNoteManager().getNoteEventProcessorManager().onTouchEvent(e);
    }

    @Override
    public void onActivate(ReaderDataHolder readerDataHolder, HandlerInitialState initialState) {
        super.onActivate(readerDataHolder, initialState);
        final StartNoteRequest request = new StartNoteRequest(readerDataHolder.getVisiblePages());
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), request, null);
    }

    public void onDeactivate(final ReaderDataHolder readerDataHolder) {
        StopNoteActionChain stopNoteActionChain = new StopNoteActionChain(true, true, true, false, false, false);
        stopNoteActionChain.execute(readerDataHolder, null);
    }


    public void afterChangePosition(final ReaderDataHolder readerDataHolder) {
        final ResumeDrawingAction action = new ResumeDrawingAction(readerDataHolder.getVisiblePages());
        action.execute(readerDataHolder, null);
    }

    @Override
    public void close(ReaderDataHolder readerDataHolder) {
        super.close(readerDataHolder);
    }
}
