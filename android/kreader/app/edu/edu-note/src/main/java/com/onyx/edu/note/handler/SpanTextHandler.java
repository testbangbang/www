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
            noteManager.post(new RequestInfoUpdateEvent(request, e));
            if (reloadPageShape) {
                noteManager.loadPageShapes();
            }
        }
    }

    public SpanTextHandler(NoteManager manager) {
        super(manager);
    }

    @Override
    public void onActivate() {
        super.onActivate();
        noteManager.registerEventBus(this);
        noteManager.openSpanTextFunc();
    }

    @Override
    public void onDeactivate() {
        noteManager.unregisterEventBus(this);
        noteManager.exitSpanTextFunc();
    }

    @Override
    public void buildFunctionBarMenuFunctionList() {
        functionBarMenuIDList = new ArrayList<>();
        functionBarMenuIDList.add(ScribbleFunctionBarMenuID.PEN_STYLE);
        functionBarMenuIDList.add(ScribbleFunctionBarMenuID.BG);
        functionBarMenuIDList.add(ScribbleFunctionBarMenuID.DELETE);
        functionBarMenuIDList.add(ScribbleFunctionBarMenuID.SPACE);
        functionBarMenuIDList.add(ScribbleFunctionBarMenuID.ENTER);
        functionBarMenuIDList.add(ScribbleFunctionBarMenuID.KEYBOARD);
    }

    @Override
    protected void buildToolBarMenuFunctionList() {
        toolBarMenuIDList = new ArrayList<>();
        toolBarMenuIDList.add(ScribbleToolBarMenuID.SWITCH_TO_NORMAL_SCRIBBLE_MODE);
        toolBarMenuIDList.add(ScribbleToolBarMenuID.UNDO);
        toolBarMenuIDList.add(ScribbleToolBarMenuID.SAVE);
        toolBarMenuIDList.add(ScribbleToolBarMenuID.REDO);
    }

    @Override
    protected void buildFunctionBarMenuSubMenuIDListSparseArray() {
        functionBarSubMenuIDMap = new SparseArray<>();
        functionBarSubMenuIDMap.put(ScribbleFunctionBarMenuID.PEN_STYLE, buildSubMenuPenStyleIDList());
        functionBarSubMenuIDMap.put(ScribbleFunctionBarMenuID.BG, buildSubMenuBGIDList());
    }

    @Override
    public void handleFunctionBarMenuFunction(int functionBarMenuID) {
        Log.e(TAG, "handleFunctionBarMenuFunction: " + functionBarMenuID);
        switch (functionBarMenuID) {
            case ScribbleFunctionBarMenuID.DELETE:
                noteManager.deleteSpan(true);
                break;
            case ScribbleFunctionBarMenuID.SPACE:
                noteManager.buildSpaceShape();
                break;
            case ScribbleFunctionBarMenuID.ENTER:
                noteManager.post(new SpanLineBreakerEvent());
                break;
            case ScribbleFunctionBarMenuID.KEYBOARD:
                noteManager.post(new ShowInputKeyBoardEvent());
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
                noteManager.post(new ShowSubMenuEvent(functionBarMenuID));
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
                noteManager.post(new ChangeScribbleModeEvent(ScribbleMode.MODE_NORMAL_SCRIBBLE));
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
        documentSaveAction.execute(noteManager, callback);
    }

    @Override
    public void prevPage() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GotoPrevPageAction prevPageAction = new GotoPrevPageAction();
                prevPageAction.execute(noteManager, new SpanBaseCallBack(true));
            }
        });
    }

    @Override
    public void nextPage() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GotoNextPageAction nextPageAction = new GotoNextPageAction();
                nextPageAction.execute(noteManager, new SpanBaseCallBack(true));
            }
        });
    }

    @Override
    public void addPage() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DocumentAddNewPageAction addNewPageAction = new DocumentAddNewPageAction();
                addNewPageAction.execute(noteManager, new SpanBaseCallBack(false));
            }
        });
    }

    @Override
    public void deletePage() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DocumentDeletePageAction deletePageAction = new DocumentDeletePageAction();
                deletePageAction.execute(noteManager, new SpanBaseCallBack(true));
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
        noteManager.syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                RedoAction reDoAction = new RedoAction();
                reDoAction.execute(noteManager, new SpanBaseCallBack(true));
            }
        });
    }

    private void unDo() {
        noteManager.syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UndoAction unDoAction = new UndoAction();
                unDoAction.execute(noteManager, new SpanBaseCallBack(true));
            }
        });
    }

    private void onBackgroundChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        int bgType = ScribbleSubMenuID.bgFromMenuID(subMenuID);
        NoteLineLayoutBackgroundChangeAction changeBGAction = new NoteLineLayoutBackgroundChangeAction(bgType, true);
        changeBGAction.execute(noteManager, new SpanBaseCallBack(false));
    }

    private void onShapeChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        int shapeType = ScribbleSubMenuID.shapeTypeFromMenuID(subMenuID);
        noteManager.getShapeDataInfo().setCurrentShapeType(shapeType);
        noteManager.sync(true, ShapeFactory.createShape(shapeType).supportDFB());
    }

    @Override
    public void onRawTouchPointListReceived() {
        noteManager.getSpanHelper().buildSpan();
    }

    @Override
    public void onDrawingTouchUp() {
        noteManager.getSpanHelper().buildSpan();
    }

}
