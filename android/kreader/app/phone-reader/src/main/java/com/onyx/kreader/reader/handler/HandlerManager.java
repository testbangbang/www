package com.onyx.kreader.reader.handler;

import android.content.Context;

import com.onyx.kreader.reader.data.ReaderDataHolder;

import java.util.HashMap;
import java.util.Map;

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

    public HandlerManager(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
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
}
