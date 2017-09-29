package com.onyx.kreader.ui.handler;

import android.view.MotionEvent;

import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by Joy on 2014/3/26.
 */
public class SideNoteHandler extends ScribbleHandler {

    public SideNoteHandler(HandlerManager p) {
        super(p);
    }

    @Override
    public boolean onSingleTapUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (e.getX() < getViewportWidth() / 2) {
            prevScreen(readerDataHolder);
        } else if (e.getX() > getViewportWidth() / 2 && e.getX() < getViewportWidth()) {
            nextScreen(readerDataHolder);
        }
        return true;
    }

    private int getViewportWidth() {
        return getParent().getReaderDataHolder().getDisplayWidth() / 2;
    }
}
