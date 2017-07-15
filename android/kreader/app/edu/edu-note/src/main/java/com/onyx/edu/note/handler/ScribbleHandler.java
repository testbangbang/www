package com.onyx.edu.note.handler;

import android.util.Log;

import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.data.ScribbleMainMenuID;
import com.onyx.edu.note.data.ScribbleSubMenuID;

import java.util.ArrayList;

/**
 * Created by solskjaer49 on 2017/6/23 17:49.
 */

public class ScribbleHandler extends BaseHandler {
    private static final String TAG = ScribbleHandler.class.getSimpleName();

    public ScribbleHandler(NoteManager mNoteManager) {
        super(mNoteManager);
    }

    @Override
    public void buildMainMenuFunctionList() {
        mMainMenuFunctionIDList = new ArrayList<>();
        mMainMenuFunctionIDList.add(ScribbleMainMenuID.PEN_STYLE);
        mMainMenuFunctionIDList.add(ScribbleMainMenuID.BG);
        mMainMenuFunctionIDList.add(ScribbleMainMenuID.ERASER);
        mMainMenuFunctionIDList.add(ScribbleMainMenuID.PEN_WIDTH);
    }

    @Override
    public void handleSubMenuFunction(int subMenuID) {
        Log.e(TAG, "handleSubMenuFunction: "+subMenuID );
        if (ScribbleSubMenuID.isThicknessGroup(subMenuID)) {

        } else if (ScribbleSubMenuID.isBackgroundGroup(subMenuID)) {

        } else if (ScribbleSubMenuID.isEraserGroup(subMenuID)) {

        } else if (ScribbleSubMenuID.isPenColorGroup(subMenuID)) {

        } else if (ScribbleSubMenuID.isPenStyleGroup(subMenuID)) {

        }
    }

}
