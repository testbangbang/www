package com.onyx.edu.note.handler;

import android.support.annotation.CallSuper;

import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.data.ScribbleSubMenuID;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solskjaer49 on 2017/5/27 12:30.
 */

public abstract class BaseHandler {
    private static final String TAG = BaseHandler.class.getSimpleName();
    protected NoteManager mNoteManager;
    protected List<Integer> mMainMenuFunctionIDList = new ArrayList<>();

    public BaseHandler(NoteManager mNoteManager) {
        this.mNoteManager = mNoteManager;
    }

    @CallSuper
    public void onActivate() {
        buildMainMenuFunctionList();
    }

    public void onDeactivate() {
    }

    public void close() {
    }

    protected abstract void buildMainMenuFunctionList();

    public final List<Integer> getMainMenuFunctionIDList() {
        return mMainMenuFunctionIDList;
    }

    public abstract void handleSubMenuFunction(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID);
}
