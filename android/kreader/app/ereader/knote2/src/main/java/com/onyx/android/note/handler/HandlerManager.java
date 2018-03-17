package com.onyx.android.note.handler;

import com.onyx.android.sdk.note.NoteManager;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lxm on 2018/2/2.
 */

public class HandlerManager {

    public static final String EPD_SHAPE_PROVIDER = "epd_shape";
    public static final String NORMAL_SHAPE_PROVIDER = "normal_shape";
    public static final String ERASE_OVERLAY_PROVIDER = "erase_overlay";
    public static final String SELECTION_PROVIDER = "selection";
    public static final String SPAN_TEXT_PROVIDER = "span_text";

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
        providerMap.put(ERASE_OVERLAY_PROVIDER, new EraseOverlayHandler(getEventBus(), getNoteManager()));
        providerMap.put(SELECTION_PROVIDER, new SelectionHandler(getEventBus(), getNoteManager()));
        providerMap.put(SPAN_TEXT_PROVIDER, new SpanHandler(getEventBus(), getNoteManager()));
    }

    public void activeProvider(String providerName) {
        providerMap.get(activeProvider).onDeactivate();
        activeProvider = providerName;
        providerMap.get(activeProvider).onActivate();
    }

    public void quit() {
        providerMap.get(activeProvider).onDeactivate();
    }

    public boolean inEpdShapeProvider() {
        return activeProvider.equals(EPD_SHAPE_PROVIDER);
    }

    public boolean inNormalShapeProvider() {
        return activeProvider.equals(NORMAL_SHAPE_PROVIDER);
    }

    public boolean inEraseOverlayProvider() {
        return activeProvider.equals(ERASE_OVERLAY_PROVIDER);
    }
}
