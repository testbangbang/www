package com.onyx.edu.note.handler;

import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.data.ScribbleMainMenuID;

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

}
