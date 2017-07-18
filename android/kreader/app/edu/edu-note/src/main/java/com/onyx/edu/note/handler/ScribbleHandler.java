package com.onyx.edu.note.handler;

import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.edu.note.NoteManager;
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

import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_BRUSH_SCRIBBLE;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_CIRCLE;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_LINE;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_PENCIL_SCRIBBLE;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_RECTANGLE;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_TRIANGLE;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_TRIANGLE_45;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_TRIANGLE_60;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_TRIANGLE_90;

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

        } else if (ScribbleSubMenuID.isBackgroundGroup(subMenuID)) {
            onBackgrounChanged(subMenuID);
        } else if (ScribbleSubMenuID.isEraserGroup(subMenuID)) {

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

    private void onBackgrounChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        int bgType = -1;
        switch (subMenuID){
            case ScribbleSubMenuID.Background.BG_EMPTY:
                bgType = NoteBackgroundType.EMPTY;
                break;
            case ScribbleSubMenuID.Background.BG_LINE:
                bgType = NoteBackgroundType.LINE;
                break;
            case ScribbleSubMenuID.Background.BG_GRID:
                bgType = NoteBackgroundType.GRID;
                break;
            case ScribbleSubMenuID.Background.BG_MUSIC:
                bgType = NoteBackgroundType.MUSIC;
                break;
            case ScribbleSubMenuID.Background.BG_MATS:
                bgType = NoteBackgroundType.MATS;
                break;
            case ScribbleSubMenuID.Background.BG_ENGLISH:
                bgType = NoteBackgroundType.ENGLISH;
                break;
            case ScribbleSubMenuID.Background.BG_TABLE_GRID:
                bgType = NoteBackgroundType.TABLE;
                break;
            case ScribbleSubMenuID.Background.BG_LINE_COLUMN:
                bgType = NoteBackgroundType.COLUMN;
                break;
            case ScribbleSubMenuID.Background.BG_LEFT_GRID:
                bgType = NoteBackgroundType.LEFT_GRID;
                break;
            case ScribbleSubMenuID.Background.BG_GRID_POINT:
                bgType = NoteBackgroundType.GRID_POINT;
                break;
            case ScribbleSubMenuID.Background.BG_LINE_1_6:
                bgType = NoteBackgroundType.LINE_1_6;
                break;
            case ScribbleSubMenuID.Background.BG_LINE_2_0:
                bgType = NoteBackgroundType.LINE_2_0;
                break;
            case ScribbleSubMenuID.Background.BG_CALENDAR:
                bgType = NoteBackgroundType.CALENDAR;
                break;
        }
        mNoteManager.getShapeDataInfo().setBackground(bgType);
        NoteBackgroundChangeAction changeBGAction = new NoteBackgroundChangeAction(bgType, !mNoteManager.inUserErasing());
    }

    private void onShapeChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        switch (subMenuID) {
            case ScribbleSubMenuID.PenStyle.NORMAL_PEN_STYLE:
                mNoteManager.getShapeDataInfo().setCurrentShapeType(SHAPE_PENCIL_SCRIBBLE);
                mNoteManager.sync(true, true);
                break;
            case ScribbleSubMenuID.PenStyle.BRUSH_PEN_STYLE:
                mNoteManager.getShapeDataInfo().setCurrentShapeType(SHAPE_BRUSH_SCRIBBLE);
                mNoteManager.sync(true, true);
                break;
            case ScribbleSubMenuID.PenStyle.TRIANGLE_STYLE:
                mNoteManager.getShapeDataInfo().setCurrentShapeType(SHAPE_TRIANGLE);
                mNoteManager.sync(true, false);
                break;
            case ScribbleSubMenuID.PenStyle.LINE_STYLE:
                mNoteManager.getShapeDataInfo().setCurrentShapeType(SHAPE_LINE);
                mNoteManager.sync(true, false);
                break;
            case ScribbleSubMenuID.PenStyle.CIRCLE_STYLE:
                mNoteManager.getShapeDataInfo().setCurrentShapeType(SHAPE_CIRCLE);
                mNoteManager.sync(true, false);
                break;
            case ScribbleSubMenuID.PenStyle.RECT_STYLE:
                mNoteManager.getShapeDataInfo().setCurrentShapeType(SHAPE_RECTANGLE);
                mNoteManager.sync(true, false);
                break;
            case ScribbleSubMenuID.PenStyle.TRIANGLE_45_STYLE:
                mNoteManager.getShapeDataInfo().setCurrentShapeType(SHAPE_TRIANGLE_45);
                mNoteManager.sync(true, false);
                break;
            case ScribbleSubMenuID.PenStyle.TRIANGLE_60_STYLE:
                mNoteManager.getShapeDataInfo().setCurrentShapeType(SHAPE_TRIANGLE_60);
                mNoteManager.sync(true, false);
                break;
            case ScribbleSubMenuID.PenStyle.TRIANGLE_90_STYLE:
                mNoteManager.getShapeDataInfo().setCurrentShapeType(SHAPE_TRIANGLE_90);
                mNoteManager.sync(true, false);
                break;
        }

    }
}
