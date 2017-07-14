package com.onyx.edu.note.scribble;

import com.onyx.edu.note.data.ScribbleMainMenuID;
import com.onyx.edu.note.data.ScribbleSubMenuID;

/**
 * Created by solskjaer49 on 2017/7/10 19:47.
 */

public interface ScribbleItemNavigator {

    void onMainMenuFunctionItem(@ScribbleMainMenuID.ScribbleMainMenuDef int mainMenuID);

    void onSubMenuFunctionItem(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID);

}
