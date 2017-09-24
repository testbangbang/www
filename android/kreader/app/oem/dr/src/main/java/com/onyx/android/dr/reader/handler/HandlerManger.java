package com.onyx.android.dr.reader.handler;

import android.view.MotionEvent;


import com.onyx.android.dr.reader.dialog.DialogAudioPlay;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderImage;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huxiaomao on 17/5/5.
 */

public class HandlerManger {
    private static final String TAG = HandlerManger.class.getSimpleName();
    public static final int READING_PROVIDER = 0;
    public static final int WORD_SELECTION_PROVIDER = 2;
    public static final int TTS_PROVIDER = 3;
    private ReaderPresenter readerPresenter;
    private int activeProviderType;
    private DialogAudioPlay dialogAudioPlay;
    public Map<Integer, BaseHandler> handlerList = null;
    private static final List<String> AUDIO_TYPE_LIST = new ArrayList<>();
    static {
        AUDIO_TYPE_LIST.add(".mp3");
    }

    public static boolean isAudio(String name){
        for(String type : AUDIO_TYPE_LIST){
            if(name.endsWith(type)){
                return true;
            }
        }
        return false;
    }

    public int getActiveProviderType() {
        return activeProviderType;
    }

    public boolean isTtsModel(){
        if(activeProviderType == TTS_PROVIDER){
            return true;
        }
        return false;
    }

    public HandlerManger(ReaderPresenter readerPresenter) {
        this.readerPresenter = readerPresenter;
        initHandler();
        dialogAudioPlay = new DialogAudioPlay(readerPresenter);
    }

    public void initHandler() {
        handlerList = new HashMap<>();
        handlerList.put(READING_PROVIDER, new ReadingHandler(readerPresenter));
        handlerList.put(WORD_SELECTION_PROVIDER, new WordSelectionHandler(readerPresenter));
        handlerList.put(TTS_PROVIDER, new TTSHandler(readerPresenter));

        activeProviderType = READING_PROVIDER;
    }

    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        return handlerList.get(activeProviderType).onKeyDown(keyCode, event);
    }


    public boolean onTouchEvent(MotionEvent event) {
        return handlerList.get(activeProviderType).onTouchEvent(event);
    }

    public boolean onActionUp(MotionEvent event) {
        return handlerList.get(activeProviderType).onActionUp(event);
    }

    public boolean onActionCancel(MotionEvent event) {
        return handlerList.get(activeProviderType).onActionCancel(event);
    }

    public boolean onDown(MotionEvent event) {
        return handlerList.get(activeProviderType).onDown(event);
    }

    public void onLongPress(MotionEvent event) {
        updateActionProviderType(WORD_SELECTION_PROVIDER);
        handlerList.get(activeProviderType).onLongPress(event);
    }

    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
        return true;
    }

    public boolean onSingleTapUp(MotionEvent event) {
        String audioName = tryPageImage(event.getX(),event.getY());
        if(StringUtils.isNotBlank(audioName)){
            dialogAudioPlay.setAudioPath(audioName);
            dialogAudioPlay.show();
            return true;
        }
        return handlerList.get(activeProviderType).onSingleTapUp(event);
    }

    private String tryPageImage(final float x, final float y) {
        for (PageInfo pageInfo : readerPresenter.getReaderViewInfo().getVisiblePages()) {
            if (!readerPresenter.getReaderUserDataInfo().hasPageImages(pageInfo)) {
                continue;
            }
            List<ReaderImage> images = readerPresenter.getReaderUserDataInfo().getPageImages(pageInfo);
            for (ReaderImage image : images) {
                if (image.getRectangle().contains(x, y)) {
                    if(isAudio(image.getName())) {
                        return image.getName();
                    }
                }
            }
        }
        return null;
    }

    public boolean onSingleTapConfirmed(MotionEvent event) {
        return handlerList.get(activeProviderType).onSingleTapConfirmed(event);
    }

    public void updateActionProviderType(int type) {
        activeProviderType = type;
    }

    public void resetTouchStartPosition() {
        handlerList.get(activeProviderType).resetTouchStartPosition();
    }

    public void setTouchStartEvent(MotionEvent event) {
        handlerList.get(activeProviderType).setTouchStartEvent(event);
    }

    public void onStop(){
        handlerList.get(activeProviderType).onStop();
        updateActionProviderType(READING_PROVIDER);
    }

    public void close(){
        handlerList.get(activeProviderType).close();
    }
}
