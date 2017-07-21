package com.onyx.edu.note.handler;

import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.scribble.ClearAllFreeShapesAction;
import com.onyx.edu.note.actions.scribble.DocumentAddNewPageAction;
import com.onyx.edu.note.actions.scribble.DocumentDeletePageAction;
import com.onyx.edu.note.actions.scribble.DocumentSaveAction;
import com.onyx.edu.note.actions.scribble.GotoNextPageAction;
import com.onyx.edu.note.actions.scribble.GotoPrevPageAction;
import com.onyx.edu.note.actions.scribble.NoteBackgroundChangeAction;
import com.onyx.edu.note.actions.scribble.RedoAction;
import com.onyx.edu.note.actions.scribble.UndoAction;
import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.edu.note.data.ScribbleSubMenuID;
import com.onyx.edu.note.data.ScribbleToolBarMenuID;

import java.util.ArrayList;

import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_ERASER;

/**
 * Created by solskjaer49 on 2017/6/23 17:49.
 */

public class ScribbleHandler extends BaseHandler {
    private static final String TAG = ScribbleHandler.class.getSimpleName();
    private BaseCallback mActionDoneCallback = new BaseCallback() {
        @Override
        public void done(BaseRequest request, Throwable e) {
            mScribbleViewModel.onRequestFinished((AsyncBaseNoteRequest) request, e);
        }
    };

    public ScribbleHandler(NoteManager mNoteManager) {
        super(mNoteManager);
    }

    @Override
    public void buildFunctionBarMenuFunctionList() {
        mFunctionBarMenuFunctionIDList = new ArrayList<>();
        mFunctionBarMenuFunctionIDList.add(ScribbleFunctionBarMenuID.PEN_STYLE);
        mFunctionBarMenuFunctionIDList.add(ScribbleFunctionBarMenuID.BG);
        mFunctionBarMenuFunctionIDList.add(ScribbleFunctionBarMenuID.ERASER);
        mFunctionBarMenuFunctionIDList.add(ScribbleFunctionBarMenuID.PEN_WIDTH);
    }

    @Override
    protected void buildToolBarMenuFunctionList() {
        mToolBarMenuFunctionIDList = new ArrayList<>();
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.SWITCH_SCRIBBLE_MODE);
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.UNDO);
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.SAVE);
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.REDO);
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.SETTING);
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.EXPORT);
    }

    @Override
    public void handleSubMenuFunction(int subMenuID) {
        Log.e(TAG, "handleSubMenuFunction: " + subMenuID);
        if (ScribbleSubMenuID.isThicknessGroup(subMenuID)) {
            onStrokeWidthChanged(subMenuID);
        } else if (ScribbleSubMenuID.isBackgroundGroup(subMenuID)) {
            onBackgroundChanged(subMenuID);
        } else if (ScribbleSubMenuID.isEraserGroup(subMenuID)) {
            onEraserChanged(subMenuID);
        } else if (ScribbleSubMenuID.isPenColorGroup(subMenuID)) {

        } else if (ScribbleSubMenuID.isPenStyleGroup(subMenuID)) {
            onShapeChanged(subMenuID);
        }
    }

    @Override
    public void handleToolBarMenuFunction(int toolBarMenuID) {
        switch (toolBarMenuID) {
            case ScribbleToolBarMenuID.SWITCH_SCRIBBLE_MODE:
                break;
            case ScribbleToolBarMenuID.EXPORT:
                break;
            case ScribbleToolBarMenuID.UNDO:
                unDo();
                break;
            case ScribbleToolBarMenuID.REDO:
                reDo();
                break;
            case ScribbleToolBarMenuID.SAVE:
                saveDocument(false, null);
                break;
            case ScribbleToolBarMenuID.SETTING:
                break;
        }
    }

    @Override
    public void prevPage() {
        mNoteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GotoPrevPageAction prevPageAction = new GotoPrevPageAction();
                prevPageAction.execute(mNoteManager, mActionDoneCallback);
            }
        });
    }

    @Override
    public void nextPage() {
        mNoteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GotoNextPageAction nextPageAction = new GotoNextPageAction();
                nextPageAction.execute(mNoteManager, mActionDoneCallback);
            }
        });
    }

    @Override
    public void addPage() {
        mNoteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DocumentAddNewPageAction addNewPageAction = new DocumentAddNewPageAction();
                addNewPageAction.execute(mNoteManager, mActionDoneCallback);
            }
        });
    }

    @Override
    public void deletePage() {
        mNoteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DocumentDeletePageAction deletePageAction = new DocumentDeletePageAction();
                deletePageAction.execute(mNoteManager, mActionDoneCallback);
            }
        });
    }

    private void reDo() {
        mNoteManager.syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                RedoAction reDoAction = new RedoAction();
                reDoAction.execute(mNoteManager, mActionDoneCallback);
            }
        });
    }

    private void unDo() {
        mNoteManager.syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UndoAction unDoAction = new UndoAction();
                unDoAction.execute(mNoteManager, mActionDoneCallback);
            }
        });
    }

    @Override
    public void saveDocument(boolean closeAfterSave, BaseCallback callback) {
        DocumentSaveAction documentSaveAction = new DocumentSaveAction(mScribbleViewModel.getCurrentDocumentUniqueID(),
                mScribbleViewModel.mNoteTitle.get(), closeAfterSave);
        documentSaveAction.execute(mNoteManager, callback);
    }

    private void onBackgroundChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        int bgType = ScribbleSubMenuID.bgFromMenuID(subMenuID);
        NoteBackgroundChangeAction changeBGAction = new NoteBackgroundChangeAction(bgType, !mNoteManager.inUserErasing());
        changeBGAction.execute(mNoteManager, mActionDoneCallback);
    }

    private void onShapeChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        int shapeType = ScribbleSubMenuID.shapeTypeFromMenuID(subMenuID);
        mNoteManager.getShapeDataInfo().setCurrentShapeType(shapeType);
        mNoteManager.sync(true, ShapeFactory.createShape(shapeType).supportDFB());
    }

    private void onEraserChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        switch (subMenuID) {
            case ScribbleSubMenuID.Eraser.ERASE_PARTIALLY:
                mNoteManager.getShapeDataInfo().setCurrentShapeType(SHAPE_ERASER);
                mNoteManager.sync(true, false);
                break;
            case ScribbleSubMenuID.Eraser.ERASE_TOTALLY:
                new ClearAllFreeShapesAction().execute(mNoteManager, mActionDoneCallback);
                break;
        }
    }

    private void onStrokeWidthChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        switch (subMenuID) {
            case ScribbleSubMenuID.Thickness.THICKNESS_ULTRA_LIGHT:
            case ScribbleSubMenuID.Thickness.THICKNESS_LIGHT:
            case ScribbleSubMenuID.Thickness.THICKNESS_NORMAL:
            case ScribbleSubMenuID.Thickness.THICKNESS_BOLD:
            case ScribbleSubMenuID.Thickness.THICKNESS_ULTRA_BOLD:
                float value = ScribbleSubMenuID.strokeWidthFromMenuId(subMenuID);
                if (mNoteManager.getShapeDataInfo().isInUserErasing()) {
                    mNoteManager.getShapeDataInfo().setCurrentShapeType(ShapeFactory.SHAPE_PENCIL_SCRIBBLE);
                }
                mNoteManager.getShapeDataInfo().setStrokeWidth(value);
                mNoteManager.syncWithCallback(true, true, mActionDoneCallback);
                break;
        }
    }
}
