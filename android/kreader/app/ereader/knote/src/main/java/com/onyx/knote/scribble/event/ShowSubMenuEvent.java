package com.onyx.knote.scribble.event;

import com.onyx.knote.data.ScribbleFunctionBarMenuID;

/**
 * Created by solskjaer49 on 2017/7/24 18:21.
 */

public class ShowSubMenuEvent {
    public ShowSubMenuEvent(int funcBarMenuID) {
        functionBarMenuID = funcBarMenuID;
    }

    private @ScribbleFunctionBarMenuID.ScribbleFunctionBarMenuDef
    int functionBarMenuID;

    public int getFunctionBarMenuID() {
        return functionBarMenuID;
    }
}
