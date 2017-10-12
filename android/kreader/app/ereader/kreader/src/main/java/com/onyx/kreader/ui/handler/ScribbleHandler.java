package com.onyx.kreader.ui.handler;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.onyx.kreader.note.actions.FlushNoteAction;
import com.onyx.kreader.note.actions.ResumeDrawingAction;
import com.onyx.kreader.note.actions.StopNoteActionChain;
import com.onyx.kreader.note.request.StartNoteRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;


/**
 * Created by Joy on 2014/3/26.
 */
public class ScribbleHandler extends BaseHandler {

    public ScribbleHandler(HandlerManager p) {
        super(p);
    }

    private boolean isEnableBigPen() {
        return true;
    }

    public void onActivate(final ReaderDataHolder readerDataHolder, final HandlerInitialState initialState) {
        final StartNoteRequest request = new StartNoteRequest(readerDataHolder.getVisiblePages(), readerDataHolder.isSideNoting());
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
            return false;
        }

        return readerDataHolder.getNoteManager().getTouchHelper().onTouchEvent(e);
    }

    private boolean inSelection(final ReaderDataHolder readerDataHolder) {
        return readerDataHolder.getNoteManager().isInSelection();
    }

    public boolean onScroll(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (inSelection(readerDataHolder)) {
            return super.onScroll(readerDataHolder, e1, e2, distanceX, distanceY);
        }
        return false;
    }

    public boolean onSingleTapUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (inSelection(readerDataHolder)) {
            return super.onSingleTapUp(readerDataHolder, e);
        }
        return false;
    }

    public boolean onScaleEnd(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        if (inSelection(readerDataHolder)) {
            return super.onScaleEnd(readerDataHolder, detector);
        }
        return false;
    }

    public boolean onScaleBegin(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        if (inSelection(readerDataHolder)) {
            return super.onScaleBegin(readerDataHolder, detector);
        }
        return false;
    }

    public boolean onScale(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector)  {
        if (inSelection(readerDataHolder)) {
            return super.onScale(readerDataHolder, detector);
        }
        return false;
    }

    public boolean onActionUp(ReaderDataHolder readerDataHolder, final float startX, final float startY, final float endX, final float endY) {
        if (inSelection(readerDataHolder)) {
            return super.onActionUp(readerDataHolder, startX, startY, endX, endY);
        }
        return false;
    }

    public void onLongPress(ReaderDataHolder readerDataHolder, final float x1, final float y1, final float x2, final float y2) {
    }

    public void beforeProcessKeyDown(final ReaderDataHolder readerDataHolder, final String action, final String args) {
        final FlushNoteAction flushNoteAction = new FlushNoteAction(readerDataHolder.getVisiblePages(), true, true, false, false);
        flushNoteAction.execute(getParent().getReaderDataHolder(), null);
    }

    public void beforePageChangeByUser(ReaderDataHolder readerDataHolder) {

    }

    public void afterChangePosition(final ReaderDataHolder readerDataHolder) {
        final ResumeDrawingAction action = new ResumeDrawingAction(readerDataHolder.getVisiblePages());
        action.execute(readerDataHolder, null);
    }

    public void close(final ReaderDataHolder readerDataHolder) {
        StopNoteActionChain stopNoteActionChain = new StopNoteActionChain(true, true, true, false, false, true);
        stopNoteActionChain.execute(readerDataHolder, null);
    }
}
