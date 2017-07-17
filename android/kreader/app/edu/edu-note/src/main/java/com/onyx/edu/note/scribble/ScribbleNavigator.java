package com.onyx.edu.note.scribble;

import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;

/**
 * Created by solskjaer49 on 2017/6/22 12:01.
 */

public interface ScribbleNavigator {
    void onFunctionBarMenuFunctionItem(@ScribbleFunctionBarMenuID.ScribbleFunctionBarMenuDef int functionBarMenuID);

    void goToSetting();

    void switchScribbleMode();
}
