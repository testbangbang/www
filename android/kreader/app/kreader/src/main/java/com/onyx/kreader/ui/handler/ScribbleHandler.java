package com.onyx.kreader.ui.handler;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.kreader.note.bridge.NoteEventProcessorManager;
import com.onyx.kreader.ui.data.ReaderDataHolder;


/**
 * Created by Joy on 2014/3/26.
 */
public class ScribbleHandler extends BaseHandler {

    public ScribbleHandler(HandlerManager p) {
        super(p);
    }

    public boolean preKeyDown(ReaderDataHolder readerDataHolder, int keyCode, KeyEvent event) {
        return true;
    }

    private boolean isEnableBigPen() {
        return true;
    }


    public void onActivate(final ReaderDataHolder readerDataHolder) {
    }

    public void onDeactivate(final ReaderDataHolder readerDataHolder) {
        readerDataHolder.getNoteManager().enableScreenPost(true);
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
                return false;
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
        return readerDataHolder.getNoteManager().getNoteEventProcessorManager().onTouchEvent(e);
    }

    public void beforeProcessKeyDown(final ReaderDataHolder readerDataHolder) {
        readerDataHolder.getNoteManager().enableScreenPost(true);
    }

}
