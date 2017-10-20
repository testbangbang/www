package com.onyx.kreader.ui.handler;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.onyx.kreader.note.actions.FlushNoteAction;
import com.onyx.kreader.note.actions.ResumeDrawingAction;
import com.onyx.kreader.note.actions.StopNoteAction;
import com.onyx.kreader.note.actions.StopNoteActionChain;
import com.onyx.kreader.note.request.StartNoteRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by Joy on 2014/3/26.
 */
public class SideNoteHandler extends BaseHandler {

    public SideNoteHandler(HandlerManager p) {
        super(p);
    }

    private boolean isEnableBigPen() {
        return true;
    }

    public void onActivate(final ReaderDataHolder readerDataHolder, final HandlerInitialState initialState) {
        final StartNoteRequest request = new StartNoteRequest(readerDataHolder.getVisiblePages(), true);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), request, null);
    }

    public void onDeactivate(final ReaderDataHolder readerDataHolder) {
        StopNoteActionChain stopNoteActionChain = new StopNoteActionChain(true, true, true, false, false, true);
        stopNoteActionChain.execute(readerDataHolder, null);
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

    @Override
    public boolean onTouchEvent(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (e.getPointerCount() > 1) {
            return true;
        }

        return readerDataHolder.getNoteManager().getTouchHelper().onTouchEvent(e);
    }

    public boolean onScroll(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (inDocRegion(e1) && inDocRegion(e2)) {
            return super.onScroll(readerDataHolder, e1, e2, distanceX, distanceY);
        }
        return true;
    }

    @Override
    public boolean onSingleTapUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (inDocRegion(e)) {
            if (e.getX() < getViewportWidth() / 2) {
                prevScreen(readerDataHolder);
            } else if (e.getX() > getViewportWidth() / 2 && e.getX() < getViewportWidth()) {
                nextScreen(readerDataHolder);
            }
        }
        return true;
    }

    public boolean onScaleEnd(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        return true;
    }

    public boolean onScaleBegin(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        setPinchZooming(true);
        return true;
    }

    public boolean onScale(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector)  {
        return true;
    }

    public boolean onActionUp(ReaderDataHolder readerDataHolder, final float startX, final float startY, final float endX, final float endY) {
        if (inDocRegion(startX, startY) && inDocRegion(endX, endY)) {
            return super.onActionUp(readerDataHolder, startX, startY, endX, endY);
        }
        return true;
    }

    public void onLongPress(ReaderDataHolder readerDataHolder, final float x1, final float y1, final float x2, final float y2) {
    }

    public void beforeProcessKeyDown(final ReaderDataHolder readerDataHolder, final String action, final String args) {
        final FlushNoteAction flushNoteAction = new FlushNoteAction(readerDataHolder.getVisiblePages(), true, true, false, false);
        flushNoteAction.execute(getParent().getReaderDataHolder(), null);
    }

    public void beforeChangePosition(ReaderDataHolder readerDataHolder) {
        new StopNoteAction(false).execute(readerDataHolder, null);
    }

    public void afterChangePosition(final ReaderDataHolder readerDataHolder) {
        new ResumeDrawingAction(readerDataHolder.getVisiblePages()).execute(readerDataHolder, null);
    }

    public void close(final ReaderDataHolder readerDataHolder) {
        StopNoteActionChain stopNoteActionChain = new StopNoteActionChain(true, true, true, false, false, true);
        stopNoteActionChain.execute(readerDataHolder, null);
    }

    private boolean inDocRegion(MotionEvent e) {
        return e.getX() < getViewportWidth();
    }

    private boolean inDocRegion(float x, float y) {
        return x < getViewportWidth();
    }

    private int getViewportWidth() {
        return getParent().getReaderDataHolder().getDisplayWidth() / 2;
    }
}
