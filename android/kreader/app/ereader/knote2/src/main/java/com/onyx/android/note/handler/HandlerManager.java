package com.onyx.android.note.handler;

import com.onyx.android.sdk.note.NoteManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lxm on 2018/2/2.
 */

public class HandlerManager {

    public static final String EPD_SHAPE_PROVIDER = "epd_shape";
    public static final String NORMAL_SHAPE_PROVIDER = "normal_shape";
    public static final String ERASE_PROVIDER = "erase";
    public static final String SELECTION_PROVIDER = "selection";

    private EventBus eventBus;
    private NoteManager noteManager;
    private String activeProvider = EPD_SHAPE_PROVIDER;
    private Map<String, BaseHandler> providerMap = new HashMap<>();

    public HandlerManager(EventBus eventBus, NoteManager noteManager) {
        this.eventBus = eventBus;
        this.noteManager = noteManager;
        initProviderMap();
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public NoteManager getNoteManager() {
        return noteManager;
    }

    private void initProviderMap() {
        providerMap.put(EPD_SHAPE_PROVIDER, new EpdShapeHandler(getEventBus(), getNoteManager()));
        providerMap.put(NORMAL_SHAPE_PROVIDER, new NormalShapeHandler(getEventBus(), getNoteManager()));
        providerMap.put(ERASE_PROVIDER, new EraseHandler(getEventBus(), getNoteManager()));
        providerMap.put(SELECTION_PROVIDER, new SelectionHandler(getEventBus(), getNoteManager()));
    }

    public void activeProvider(String providerName) {
        providerMap.get(activeProvider).onDeactivate();
        activeProvider = providerName;
        providerMap.get(activeProvider).onActivate();
    }

    public void quit() {
        providerMap.get(activeProvider).onDeactivate();
    }

    public boolean inEraseProvider() {
        return activeProvider.equals(ERASE_PROVIDER);
    }
}
