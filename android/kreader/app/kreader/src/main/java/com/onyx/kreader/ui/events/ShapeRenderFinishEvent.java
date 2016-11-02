package com.onyx.kreader.ui.events;

/**
 * Created by zhuzeng on 10/14/16.
 */

public class ShapeRenderFinishEvent {

    private int uniqueId;

    public static ShapeRenderFinishEvent shapeReadyEvent() {
        return shapeReadyEventWithUniqueId(Integer.MAX_VALUE);
    }

    public static ShapeRenderFinishEvent shapeReadyEventWithUniqueId(int id) {
        final ShapeRenderFinishEvent event = new ShapeRenderFinishEvent();
        event.setUniqueId(id);
        return event;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }
}
