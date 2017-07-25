package com.onyx.edu.note.handler;

import android.support.annotation.CallSuper;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.edu.note.data.ScribbleSubMenuID;
import com.onyx.edu.note.data.ScribbleToolBarMenuID;
import com.onyx.edu.note.scribble.event.HandlerActivateEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solskjaer49 on 2017/5/27 12:30.
 */

public abstract class BaseHandler {
    private static final String TAG = BaseHandler.class.getSimpleName();
    protected NoteManager mNoteManager;

    List<Integer> mFunctionBarMenuFunctionIDList = new ArrayList<>();
    List<Integer> mToolBarMenuFunctionIDList = new ArrayList<>();

    public BaseHandler(NoteManager mNoteManager) {
        this.mNoteManager = mNoteManager;
    }

    @CallSuper
    public void onActivate() {
        buildFunctionBarMenuFunctionList();
        buildToolBarMenuFunctionList();
        EventBus.getDefault().post(new HandlerActivateEvent(mFunctionBarMenuFunctionIDList, mToolBarMenuFunctionIDList));
    }

    public void onDeactivate() {
    }

    public void close() {
    }

    protected abstract void buildFunctionBarMenuFunctionList();

    protected abstract void buildToolBarMenuFunctionList();

    public abstract void handleFunctionBarMenuFunction(@ScribbleFunctionBarMenuID.ScribbleFunctionBarMenuDef int functionBarMenuID);

    public abstract void handleSubMenuFunction(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID);

    public abstract void handleToolBarMenuFunction(String uniqueID, String title,
                                                   @ScribbleToolBarMenuID.ScribbleToolBarMenuDef int toolBarMenuID);

    public abstract void saveDocument(String uniqueID, String title, boolean closeAfterSave, BaseCallback callback);

    public abstract void prevPage();

    public abstract void nextPage();

    public abstract void addPage();

    public abstract void deletePage();

}
