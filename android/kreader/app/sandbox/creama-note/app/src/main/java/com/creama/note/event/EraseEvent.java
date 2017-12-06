package com.creama.note.event;

/**
 * Created by solskjaer49 on 2017/11/18 17:51.
 */

public class EraseEvent {
    public EraseEvent(boolean eraseAll) {
        this.eraseAll = eraseAll;
    }

    public boolean isEraseAll() {
        return eraseAll;
    }

    private boolean eraseAll = false;
}
