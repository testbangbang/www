package com.onyx.knote.scribble.event;

import com.onyx.android.sdk.scribble.data.ScribbleMode;

/**
 * Created by solskjaer49 on 2017/7/21 12:05.
 */

public class ChangeScribbleModeEvent {
    public int getTargetScribbleMode() {
        return targetScribbleMode;
    }

    public ChangeScribbleModeEvent(int scribbleMode) {
        targetScribbleMode = scribbleMode;
    }

    @ScribbleMode.ScribbleModeDef
    private
    int targetScribbleMode;
}
