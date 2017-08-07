package com.onyx.edu.note.handler;

import android.util.Log;
import android.util.SparseArray;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.edu.note.actions.scribble.DocumentAddNewPageAction;
import com.onyx.edu.note.actions.scribble.DocumentDeletePageAction;
import com.onyx.edu.note.actions.scribble.DocumentSaveAction;
import com.onyx.edu.note.actions.scribble.GotoNextPageAction;
import com.onyx.edu.note.actions.scribble.GotoPrevPageAction;
import com.onyx.edu.note.actions.scribble.NoteLineLayoutBackgroundChangeAction;
import com.onyx.edu.note.actions.scribble.RedoAction;
import com.onyx.edu.note.actions.scribble.UndoAction;
import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.android.sdk.scribble.data.ScribbleMode;
import com.onyx.edu.note.data.ScribbleSubMenuID;
import com.onyx.edu.note.data.ScribbleToolBarMenuID;
import com.onyx.edu.note.scribble.event.ChangeScribbleModeEvent;
import com.onyx.edu.note.scribble.event.RequestInfoUpdateEvent;
import com.onyx.edu.note.scribble.event.ShowInputKeyBoardEvent;
import com.onyx.edu.note.scribble.event.ShowSubMenuEvent;
import com.onyx.edu.note.scribble.event.SpanLineBreakerEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_EMPTY;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_LINE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.BRUSH_PEN_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.CIRCLE_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.LINE_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.NORMAL_PEN_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.RECT_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.TRIANGLE_45_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.TRIANGLE_60_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.TRIANGLE_90_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.TRIANGLE_STYLE;

/**
 * Created by solskjaer49 on 2017/5/27 12:33.
 */

public class SpanTextHandler extends BaseHandler {
    private static final String TAG = SpanTextHandler.class.getSimpleName();

    private class SpanBaseCallBack extends BaseCallback {
        public SpanBaseCallBack(boolean reloadPageShape) {
            this.reloadPageShape = reloadPageShape;
        }

        private boolean reloadPageShape = false;

        @Override
        public void done(BaseRequest request, Throwable e) {
            EventBus.getDefault().post(new RequestInfoUpdateEvent(request, e));
            if (reloadPageShape) {
                mNoteManager.loadPageShapes();
            }
        }
    }

    public SpanTextHandler(NoteManager manager) {
        super(manager);
    }

    @Override
    public void onActivate() {
        super.onActivate();
        mNoteManager.openSpanTextFunc();
    }

    @Override
    public void onDeactivate() {
        mNoteManager.exitSpanTextFunc();
    }

    @Override
    public void buildFunctionBarMenuFunctionList() {
        mFunctionBarMenuFunctionIDList = new ArrayList<>();
        mFunctionBarMenuFunctionIDList.add(ScribbleFunctionBarMenuID.PEN_STYLE);
        mFunctionBarMenuFunctionIDList.add(ScribbleFunctionBarMenuID.BG);
        mFunctionBarMenuFunctionIDList.add(ScribbleFunctionBarMenuID.DELETE);
        mFunctionBarMenuFunctionIDList.add(ScribbleFunctionBarMenuID.SPACE);
        mFunctionBarMenuFunctionIDList.add(ScribbleFunctionBarMenuID.ENTER);
        mFunctionBarMenuFunctionIDList.add(ScribbleFunctionBarMenuID.KEYBOARD);
    }

    @Override
    protected void buildToolBarMenuFunctionList() {
        mToolBarMenuFunctionIDList = new ArrayList<>();
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.SWITCH_TO_NORMAL_SCRIBBLE_MODE);
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.UNDO);
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.SAVE);
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.REDO);
    }

    @Override
    protected void buildFunctionBarMenuSubMenuIDListSparseArray() {
        mFunctionBarMenuSubMenuIDListSparseArray = new SparseArray<>();
        mFunctionBarMenuSubMenuIDListSparseArray.put(ScribbleFunctionBarMenuID.PEN_STYLE, buildSubMenuPenStyleIDList());
        mFunctionBarMenuSubMenuIDListSparseArray.put(ScribbleFunctionBarMenuID.BG, buildSubMenuBGIDList());
    }

    @Override
    public void handleFunctionBarMenuFunction(int functionBarMenuID) {
        Log.e(TAG, "handleFunctionBarMenuFunction: " + functionBarMenuID);
        switch (functionBarMenuID) {
            case ScribbleFunctionBarMenuID.DELETE:
                mNoteManager.deleteSpan(true);
                break;
            case ScribbleFunctionBarMenuID.SPACE:
                mNoteManager.buildSpaceShape();
                break;
            case ScribbleFunctionBarMenuID.ENTER:
                EventBus.getDefault().post(new SpanLineBreakerEvent());
                break;
            case ScribbleFunctionBarMenuID.KEYBOARD:
                EventBus.getDefault().post(new ShowInputKeyBoardEvent());
                break;
            case ScribbleFunctionBarMenuID.ADD_PAGE:
                addPage();
                break;
            case ScribbleFunctionBarMenuID.DELETE_PAGE:
                deletePage();
                break;
            case ScribbleFunctionBarMenuID.NEXT_PAGE:
                nextPage();
                break;
            case ScribbleFunctionBarMenuID.PREV_PAGE:
                prevPage();
                break;
            default:
                EventBus.getDefault().post(new ShowSubMenuEvent(functionBarMenuID));
                break;
        }
    }

    @Override
    public void handleSubMenuFunction(int subMenuID) {
        Log.e(TAG, "handleSubMenuFunction: " + subMenuID);
        if (ScribbleSubMenuID.isBackgroundGroup(subMenuID)) {
            onBackgroundChanged(subMenuID);
        } else if (ScribbleSubMenuID.isPenStyleGroup(subMenuID)) {
            onShapeChanged(subMenuID);
        }
    }

    @Override
    public void handleToolBarMenuFunction(String uniqueID, String title, int toolBarMenuID) {
        switch (toolBarMenuID) {
            case ScribbleToolBarMenuID.SWITCH_TO_NORMAL_SCRIBBLE_MODE:
                EventBus.getDefault().post(new ChangeScribbleModeEvent(ScribbleMode.MODE_NORMAL_SCRIBBLE));
                break;
            case ScribbleToolBarMenuID.SAVE:
                saveDocument(uniqueID, title, false, null);
                break;
            case ScribbleToolBarMenuID.UNDO:
                unDo();
                break;
            case ScribbleToolBarMenuID.REDO:
                reDo();
                break;
            case ScribbleToolBarMenuID.SETTING:
                break;
        }
    }

    @Override
    public void saveDocument(String uniqueID, String title, boolean closeAfterSave, BaseCallback callback) {
        DocumentSaveAction documentSaveAction = new DocumentSaveAction(uniqueID,
                title, closeAfterSave);
        documentSaveAction.execute(mNoteManager, callback);
    }

    @Override
    public void prevPage() {
        mNoteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GotoPrevPageAction prevPageAction = new GotoPrevPageAction();
                prevPageAction.execute(mNoteManager, new SpanBaseCallBack(true));
            }
        });
    }

    @Override
    public void nextPage() {
        mNoteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GotoNextPageAction nextPageAction = new GotoNextPageAction();
                nextPageAction.execute(mNoteManager, new SpanBaseCallBack(true));
            }
        });
    }

    @Override
    public void addPage() {
        mNoteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DocumentAddNewPageAction addNewPageAction = new DocumentAddNewPageAction();
                addNewPageAction.execute(mNoteManager, new SpanBaseCallBack(false));
            }
        });
    }

    @Override
    public void deletePage() {
        mNoteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DocumentDeletePageAction deletePageAction = new DocumentDeletePageAction();
                deletePageAction.execute(mNoteManager, new SpanBaseCallBack(true));
            }
        });
    }

    private List<Integer> buildSubMenuPenStyleIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(NORMAL_PEN_STYLE);
        resultList.add(BRUSH_PEN_STYLE);
        resultList.add(LINE_STYLE);
        resultList.add(TRIANGLE_STYLE);
        resultList.add(CIRCLE_STYLE);
        resultList.add(RECT_STYLE);
        resultList.add(TRIANGLE_45_STYLE);
        resultList.add(TRIANGLE_60_STYLE);
        resultList.add(TRIANGLE_90_STYLE);
        return resultList;
    }

    private  List<Integer> buildSubMenuBGIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(BG_EMPTY);
        resultList.add(BG_LINE);
        return resultList;
    }

    private void reDo() {
        mNoteManager.syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                RedoAction reDoAction = new RedoAction();
                reDoAction.execute(mNoteManager, new SpanBaseCallBack(true));
            }
        });
    }

    private void unDo() {
        mNoteManager.syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UndoAction unDoAction = new UndoAction();
                unDoAction.execute(mNoteManager, new SpanBaseCallBack(true));
            }
        });
    }

    private void onBackgroundChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        int bgType = ScribbleSubMenuID.bgFromMenuID(subMenuID);
        NoteLineLayoutBackgroundChangeAction changeBGAction = new NoteLineLayoutBackgroundChangeAction(bgType, true);
        changeBGAction.execute(mNoteManager, new SpanBaseCallBack(false));
    }

    private void onShapeChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        int shapeType = ScribbleSubMenuID.shapeTypeFromMenuID(subMenuID);
        mNoteManager.getShapeDataInfo().setCurrentShapeType(shapeType);
        mNoteManager.sync(true, ShapeFactory.createShape(shapeType).supportDFB());
    }
}
