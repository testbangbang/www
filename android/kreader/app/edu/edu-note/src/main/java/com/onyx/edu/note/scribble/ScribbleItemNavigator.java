package com.onyx.edu.note.scribble;

import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.edu.note.data.ScribbleSubMenuID;
import com.onyx.edu.note.data.ScribbleToolBarMenuID;

/**
 * Created by solskjaer49 on 2017/7/10 19:47.
 */

public interface ScribbleItemNavigator {

    void onFunctionBarMenuFunctionItem(@ScribbleFunctionBarMenuID.ScribbleFunctionBarMenuDef int functionBarMenuID);

    void onSubMenuFunctionItem(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID);

    void onToolBarMenuFunctionItem(@ScribbleToolBarMenuID.ScribbleToolBarMenuDef int toolBarMenuID);
}
