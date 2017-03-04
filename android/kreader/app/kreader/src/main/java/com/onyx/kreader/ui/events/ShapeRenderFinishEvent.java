package com.onyx.kreader.ui.events;

/**
 * Created by zhuzeng on 10/14/16.
 */

public class ShapeRenderFinishEvent {

    private int uniqueId;
    private boolean useFullUpdate = false;

    public static ShapeRenderFinishEvent shapeReadyEvent() {
        return shapeReadyEventWithUniqueId(Integer.MAX_VALUE);
    }

    public static ShapeRenderFinishEvent shapeReadyEventWithFullUpdate() {
        final ShapeRenderFinishEvent event = shapeReadyEventWithUniqueId(Integer.MAX_VALUE);
        event.setUseFullUpdate(true);
        return event;
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

    public boolean isUseFullUpdate() {
        return useFullUpdate;
    }

    public void setUseFullUpdate(boolean useFullUpdate) {
        this.useFullUpdate = useFullUpdate;
    }
}
