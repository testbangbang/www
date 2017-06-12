package com.onyx.edu.reader.ui.events;

/**
 * Created by ming on 4/10/2017.
 */

public class StartNoteDrawingEvent {

    private boolean showFullToolbar;

    public StartNoteDrawingEvent(boolean showFullToolbar) {
        this.showFullToolbar = showFullToolbar;
    }

    public boolean isShowFullToolbar() {
        return showFullToolbar;
    }

    public static StartNoteDrawingEvent create(boolean showFullToolbar) {
        return new StartNoteDrawingEvent(showFullToolbar);
    }
}
