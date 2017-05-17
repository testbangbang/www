package com.onyx.phone.reader.reader.handler;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.onyx.phone.reader.reader.data.ReaderDataHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ming on 2017/4/13.
 */

public class HandlerManager {

    public static final String READING_PROVIDER = "rp";
    public static final String WORD_SELECTION_PROVIDER = "wp";
    public static final String SCRIBBLE_PROVIDER = "scribble";
    public static final String ERASER_PROVIDER = "eraser";
    public static final String TTS_PROVIDER = "tts";

    private Map<String, BaseHandler> providerMap = new HashMap<String, BaseHandler>();
    private ReaderDataHolder readerDataHolder;
    private String activeProviderName;
    private PointF touchStartPosition;
    private AtomicBoolean enable = new AtomicBoolean();

    public HandlerManager(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
        initProviderMap(readerDataHolder.getContext());
    }

    private void initProviderMap(final Context context) {
        providerMap.put(READING_PROVIDER, new ReadingHandler(this));
        providerMap.put(WORD_SELECTION_PROVIDER, new WordSelectionHandler(this));
        providerMap.put(SCRIBBLE_PROVIDER, new ScribbleHandler(this));
        providerMap.put(TTS_PROVIDER, new TtsHandler(this));
        activeProviderName = READING_PROVIDER;
    }

    public void setActiveProvider(final String providerName) {
        getActiveProvider().onDeactivate(readerDataHolder);
        activeProviderName = providerName;
        getActiveProvider().onActivate(readerDataHolder);
    }

    private BaseHandler getActiveProvider() {
        return providerMap.get(activeProviderName);
    }

    public void setEnable(boolean e) {
        enable.set(e);
    }

    public boolean isEnable() {
        return enable.get();
    }

    public void setTouchStartEvent(MotionEvent event) {
        if (touchStartPosition == null) {
            touchStartPosition = new PointF(event.getX(), event.getY());
        }
    }

    public void resetToDefaultProvider() {
        setActiveProvider(READING_PROVIDER);
    }

    public PointF getTouchStartPosition() {
        return touchStartPosition;
    }

    public String getActiveProviderName() {
        return activeProviderName;
    }

    public boolean onScroll(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!isEnable()) {
            return false;
        }
        return getActiveProvider().onScroll(readerDataHolder, e1, e2, distanceX, distanceY);
    }

    public void onLongPress(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (!isEnable()) {
            return;
        }
        if (getActiveProviderName().equals(READING_PROVIDER)){
            setActiveProvider(HandlerManager.WORD_SELECTION_PROVIDER);
        }
        getActiveProvider().onLongPress(readerDataHolder, getTouchStartPosition().x, getTouchStartPosition().y, e.getX(), e.getY());
    }

    public void onTouchEvent(final ReaderDataHolder readerDataHolder, final MotionEvent e) {
        if (!isEnable()) {
            return;
        }
        getActiveProvider().onTouchEvent(readerDataHolder, e);
    }

    public boolean onFling(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (!isEnable()) {
            return false;
        }
        return getActiveProvider().onFling(readerDataHolder, e1, e2, velocityX, velocityY);
    }

    public boolean onDown(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (!isEnable()) {
            return false;
        }
        return getActiveProvider().onDown(readerDataHolder, e);
    }

    public boolean onSingleTapUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (!isEnable()) {
            return false;
        }
        return getActiveProvider().onSingleTapUp(readerDataHolder, e);
    }

    public boolean onSingleTapConfirmed(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (!isEnable()) {
            return false;
        }
        return getActiveProvider().onSingleTapConfirmed(readerDataHolder, e);
    }
}
