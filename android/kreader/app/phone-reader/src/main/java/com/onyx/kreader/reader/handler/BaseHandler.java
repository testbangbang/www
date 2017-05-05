package com.onyx.kreader.reader.handler;

import android.view.MotionEvent;

import com.onyx.kreader.action.NextScreenAction;
import com.onyx.kreader.action.PrevScreenAction;
import com.onyx.kreader.reader.data.ReaderDataHolder;

/**
 * Created by ming on 2017/4/13.
 */

public abstract class BaseHandler {

    private HandlerManager handlerManager;

    public BaseHandler(HandlerManager handlerManager) {
        this.handlerManager = handlerManager;
    }

    public HandlerManager getHandlerManager() {
        return handlerManager;
    }

    public void onActivate(final ReaderDataHolder readerDataHolder) {}

    public void onDeactivate(final ReaderDataHolder readerDataHolder) {}

    public boolean onDown(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return false;
    }
    public boolean onSingleTapConfirmed(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return false;
    }

    public boolean onFling(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public boolean onSingleTapUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (e.getX() > readerDataHolder.getDisplayWidth() * 2 / 3) {
            nextScreen(readerDataHolder);
        }else if (e.getX() < readerDataHolder.getDisplayWidth() / 3) {
            prevScreen(readerDataHolder);
        }
        return true;
    }

    public void onLongPress(ReaderDataHolder readerDataHolder, final float x1, final float y1, final float x2, final float y2) {

    }

    public boolean onScroll(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    private void nextScreen(final ReaderDataHolder readerDataHolder) {
        new NextScreenAction().execute(readerDataHolder, null);
    }

    private void prevScreen(final ReaderDataHolder readerDataHolder) {
        new PrevScreenAction().execute(readerDataHolder, null);
    }

    public boolean onTouchEvent(ReaderDataHolder readerDataHolder,MotionEvent e) {
        return true;
    }
}
