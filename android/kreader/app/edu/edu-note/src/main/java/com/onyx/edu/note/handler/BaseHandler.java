package com.onyx.edu.note.handler;

import android.support.annotation.CallSuper;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.data.ScribbleSubMenuID;
import com.onyx.edu.note.data.ScribbleToolBarMenuID;
import com.onyx.edu.note.scribble.ScribbleViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solskjaer49 on 2017/5/27 12:30.
 */

public abstract class BaseHandler {
    private static final String TAG = BaseHandler.class.getSimpleName();
    protected NoteManager mNoteManager;

    public void setScribbleViewModel(ScribbleViewModel mScribbleViewModel) {
        this.mScribbleViewModel = mScribbleViewModel;
    }

    protected ScribbleViewModel mScribbleViewModel;
    protected List<Integer> mFunctionBarMenuFunctionIDList = new ArrayList<>();
    protected List<Integer> mToolBarMenuFunctionIDList = new ArrayList<>();

    public BaseHandler(NoteManager mNoteManager) {
        this.mNoteManager = mNoteManager;
    }

    @CallSuper
    public void onActivate() {
        buildFunctionBarMenuFunctionList();
        buildToolBarMenuFunctionList();
        mScribbleViewModel.setFunctionBarMenuIDList(mFunctionBarMenuFunctionIDList);
        mScribbleViewModel.setToolBarMenuIDList(mToolBarMenuFunctionIDList);
    }

    public void onDeactivate() {
    }

    public void close() {
    }

    protected abstract void buildFunctionBarMenuFunctionList();

    protected abstract void buildToolBarMenuFunctionList();

    public final List<Integer> getFunctionBarMenuFunctionIDList() {
        return mFunctionBarMenuFunctionIDList;
    }

    public abstract void handleSubMenuFunction(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID);

    public abstract void handleToolBarMenuFunction(@ScribbleToolBarMenuID.ScribbleToolBarMenuDef int toolBarMenuID);

    public final List<Integer> getToolBarMenuFunctionIDList() {
        return mToolBarMenuFunctionIDList;
    }

    public abstract void saveDocument(boolean closeAfterSave, BaseCallback callback);

    public abstract void prevPage();
    public abstract void nextPage();
    public abstract void addPage();
    public abstract void deletePage();

}
