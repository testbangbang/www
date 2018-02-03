package com.onyx.jdread.reader.handler;

import android.view.MotionEvent;

import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.data.RegionFunctionManager;
import com.onyx.jdread.reader.event.ShowCloseDocumentDialogEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxiaomao on 17/5/5.
 */

public class HandlerManger {
    private static final String TAG = HandlerManger.class.getSimpleName();
    public static final int READING_PROVIDER = 0;
    public static final int WORD_SELECTION_PROVIDER = 2;
    public static final int TTS_PROVIDER = 3;
    private ReaderDataHolder readerDataHolder;
    private int activeProviderType;

    public Map<Integer, BaseHandler> handlers = null;

    public boolean isTtsModel() {
        if (activeProviderType == TTS_PROVIDER) {
            return true;
        }
        return false;
    }

    public HandlerManger(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;

        initHandler();
    }

    public void initHandler() {
        handlers = new HashMap<>();
        handlers.put(READING_PROVIDER, new ReadingHandler(readerDataHolder));
        handlers.put(WORD_SELECTION_PROVIDER, new WordSelectionHandler(readerDataHolder));

        activeProviderType = READING_PROVIDER;
    }

    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        return getActiveProvider().onKeyDown(keyCode, event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return getActiveProvider().onTouchEvent(event);
    }

    public boolean onActionUp(MotionEvent event) {
        return getActiveProvider().onActionUp(event);
    }

    public boolean onActionCancel(MotionEvent event) {
        return getActiveProvider().onActionCancel(event);
    }

    public boolean onDown(MotionEvent event) {
        return getActiveProvider().onDown(event);
    }

    public void onLongPress(MotionEvent event) {
        updateActionProviderType(WORD_SELECTION_PROVIDER);
        handlers.get(activeProviderType).onLongPress(event);
    }

    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
        return true;
    }

    public boolean onSingleTapUp(MotionEvent event) {
        if (readerDataHolder.getSelectMenuModel().getIsShowSelectMenu().get()) {
            readerDataHolder.getSelectMenuModel().setIsShowSelectMenu(false);
            return true;
        }
        if (getActiveProvider().onSingleTapUp(event)) {
            return true;
        }
        return processSingleTapUp(event);
    }

    private boolean processSingleTapUp(MotionEvent event) {
        panFinished((int) event.getX(), (int) event.getY());
        return true;
    }

    public void panFinished(int offsetX, int offsetY) {
        if (!readerDataHolder.getReaderViewInfo().canPan()) {
            RegionFunctionManager.processRegionFunction(readerDataHolder, offsetX, offsetY);
            return;
        }
    }

    public boolean onSingleTapConfirmed(MotionEvent event) {
        return getActiveProvider().onSingleTapConfirmed(event);
    }

    public void updateActionProviderType(int type) {
        activeProviderType = type;
    }

    public int getActiveProviderType() {
        return activeProviderType;
    }

    public BaseHandler getActiveProvider() {
        return handlers.get(activeProviderType);
    }

    public void resetTouchStartPosition() {
        getActiveProvider().resetTouchStartPosition();
    }

    public void setTouchStartEvent(MotionEvent event) {
        getActiveProvider().setTouchStartEvent(event);
    }

    public void onStop() {
        getActiveProvider().onStop();
        updateActionProviderType(READING_PROVIDER);
    }

    public void showCloseDocumentDialog(){
        readerDataHolder.getEventBus().post(new ShowCloseDocumentDialogEvent());
    }
}
