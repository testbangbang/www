package com.onyx.android.dr.reader.event;

/**
 * Created by zhuzeng on 10/13/16.
 */

public class PinchZoomEvent {
    public enum Command {SHOW, HIDE}
    public enum Type { FONT_SIZE, SCALE }

    public Command command;
    public Type type;
    public int value;

    private PinchZoomEvent() {
    }

    public static PinchZoomEvent create(Command command, Type type, int value) {
        PinchZoomEvent event = new PinchZoomEvent();
        event.command = command;
        event.type = type;
        event.value = value;
        return event;
    }
}
