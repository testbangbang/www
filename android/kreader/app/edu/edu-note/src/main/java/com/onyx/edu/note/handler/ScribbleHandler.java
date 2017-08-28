package com.onyx.edu.note.handler;

import android.util.Log;
import android.util.SparseArray;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.ScribbleMode;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.ui.dialog.DialogCustomLineWidth;
import com.onyx.edu.note.actions.scribble.ClearAllFreeShapesAction;
import com.onyx.edu.note.actions.scribble.DocumentAddNewPageAction;
import com.onyx.edu.note.actions.scribble.DocumentDeletePageAction;
import com.onyx.edu.note.actions.scribble.DocumentSaveAction;
import com.onyx.edu.note.actions.scribble.GotoNextPageAction;
import com.onyx.edu.note.actions.scribble.GotoPrevPageAction;
import com.onyx.edu.note.actions.scribble.NoteBackgroundChangeAction;
import com.onyx.edu.note.actions.scribble.RedoAction;
import com.onyx.edu.note.actions.scribble.RenderInBackgroundAction;
import com.onyx.edu.note.actions.scribble.UndoAction;
import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.edu.note.data.ScribbleSubMenuID;
import com.onyx.edu.note.data.ScribbleToolBarMenuID;
import com.onyx.edu.note.scribble.event.ChangeScribbleModeEvent;
import com.onyx.edu.note.scribble.event.CustomWidthEvent;
import com.onyx.edu.note.scribble.event.RequestInfoUpdateEvent;
import com.onyx.edu.note.scribble.event.ShowSubMenuEvent;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_ERASER;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_CALENDAR;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_EMPTY;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_ENGLISH;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_GRID;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_GRID_5_5;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_GRID_POINT;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_LEFT_GRID;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_LINE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_LINE_1_6;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_LINE_2_0;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_LINE_COLUMN;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_MATS;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_MUSIC;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_TABLE_GRID;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Eraser.ERASE_PARTIALLY;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Eraser.ERASE_TOTALLY;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.BRUSH_PEN_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.CIRCLE_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.LINE_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.NORMAL_PEN_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.RECT_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.TRIANGLE_45_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.TRIANGLE_60_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.TRIANGLE_90_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.TRIANGLE_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Thickness.THICKNESS_BOLD;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Thickness.THICKNESS_CUSTOM_BOLD;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Thickness.THICKNESS_LIGHT;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Thickness.THICKNESS_NORMAL;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Thickness.THICKNESS_ULTRA_LIGHT;

/**
 * Created by solskjaer49 on 2017/6/23 17:49.
 */

public class ScribbleHandler extends BaseHandler {
    private static final String TAG = ScribbleHandler.class.getSimpleName();
    private BaseCallback mActionDoneCallback = new BaseCallback() {
        @Override
        public void done(BaseRequest request, Throwable e) {
            mNoteManager.post(new RequestInfoUpdateEvent(request, e));
        }
    };

    public ScribbleHandler(NoteManager mNoteManager) {
        super(mNoteManager);
    }

    @Override
    public void onActivate() {
        super.onActivate();
        mNoteManager.registerEventBus(this);
        mNoteManager.sync(true, true);
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        mNoteManager.unregisterEventBus(this);
    }

    @Override
    public void buildFunctionBarMenuFunctionList() {
        mFunctionBarMenuFunctionIDList = new ArrayList<>();
        mFunctionBarMenuFunctionIDList.add(ScribbleFunctionBarMenuID.PEN_STYLE);
        mFunctionBarMenuFunctionIDList.add(ScribbleFunctionBarMenuID.BG);
        mFunctionBarMenuFunctionIDList.add(ScribbleFunctionBarMenuID.ERASER);
        mFunctionBarMenuFunctionIDList.add(ScribbleFunctionBarMenuID.PEN_WIDTH);
        mFunctionBarMenuFunctionIDList.add(ScribbleFunctionBarMenuID.SHAPE_SELECT);
    }

    @Override
    protected void buildToolBarMenuFunctionList() {
        mToolBarMenuFunctionIDList = new ArrayList<>();
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.SWITCH_TO_SPAN_SCRIBBLE_MODE);
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.UNDO);
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.SAVE);
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.REDO);
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.SETTING);
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.EXPORT);
    }

    @Override
    protected void buildFunctionBarMenuSubMenuIDListSparseArray() {
        mFunctionBarMenuSubMenuIDListSparseArray = new SparseArray<>();
        mFunctionBarMenuSubMenuIDListSparseArray.put(ScribbleFunctionBarMenuID.PEN_WIDTH, buildSubMenuThicknessIDList());
        mFunctionBarMenuSubMenuIDListSparseArray.put(ScribbleFunctionBarMenuID.BG, buildSubMenuBGIDList());
        mFunctionBarMenuSubMenuIDListSparseArray.put(ScribbleFunctionBarMenuID.ERASER, buildSubMenuEraserIDList());
        mFunctionBarMenuSubMenuIDListSparseArray.put(ScribbleFunctionBarMenuID.PEN_STYLE, buildSubMenuPenStyleIDList());
    }

    @Override
    public void handleFunctionBarMenuFunction(int functionBarMenuID) {
        switch (functionBarMenuID) {
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
            case ScribbleFunctionBarMenuID.SHAPE_SELECT:
                onSetShapeSelectModeChanged();
                break;
            default:
                mNoteManager.post(new ShowSubMenuEvent(functionBarMenuID));
                break;
        }
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
        } else if (ScribbleSubMenuID.isPenStyleGroup(subMenuID)) {
            onShapeChanged(subMenuID);
        } else if (ScribbleSubMenuID.isPenColorGroup(subMenuID)) {

        }
    }

    @Override
    public void handleToolBarMenuFunction(String uniqueID, String title, int toolBarMenuID) {
        switch (toolBarMenuID) {
            case ScribbleToolBarMenuID.SWITCH_TO_SPAN_SCRIBBLE_MODE:
                mNoteManager.post(new ChangeScribbleModeEvent(ScribbleMode.MODE_SPAN_SCRIBBLE));
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
                saveDocument(uniqueID, title, false, null);
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
    public void saveDocument(String uniqueID, String title, boolean closeAfterSave, BaseCallback callback) {
        DocumentSaveAction documentSaveAction = new DocumentSaveAction(uniqueID,
                title, closeAfterSave);
        documentSaveAction.execute(mNoteManager, callback);
    }

    private List<Integer> buildSubMenuThicknessIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(THICKNESS_ULTRA_LIGHT);
        resultList.add(THICKNESS_LIGHT);
        resultList.add(THICKNESS_NORMAL);
        resultList.add(THICKNESS_BOLD);
        resultList.add(THICKNESS_CUSTOM_BOLD);
        return resultList;
    }

    private  List<Integer> buildSubMenuBGIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(BG_EMPTY);
        resultList.add(BG_LINE);
        resultList.add(BG_LEFT_GRID);
        resultList.add(BG_GRID_5_5);
        resultList.add(BG_GRID);
        resultList.add(BG_MATS);
        resultList.add(BG_MUSIC);
        resultList.add(BG_ENGLISH);
        resultList.add(BG_LINE_1_6);
        resultList.add(BG_LINE_2_0);
        resultList.add(BG_LINE_COLUMN);
        resultList.add(BG_TABLE_GRID);
        resultList.add(BG_CALENDAR);
        resultList.add(BG_GRID_POINT);
        return resultList;
    }

    private List<Integer> buildSubMenuEraserIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(ERASE_PARTIALLY);
        resultList.add(ERASE_TOTALLY);
        return resultList;
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

    private void onSetShapeSelectModeChanged(){
        Log.e(TAG, "onSetShapeSelectModeChanged: ");
        mNoteManager.post(new ChangeScribbleModeEvent(ScribbleMode.MODE_SHAPE_TRANSFORM));
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
                mNoteManager.setStrokeWidth(ScribbleSubMenuID.strokeWidthFromMenuId(subMenuID), mActionDoneCallback);
                break;
            case ScribbleSubMenuID.Thickness.THICKNESS_CUSTOM_BOLD:
                CustomWidthEvent event = new CustomWidthEvent(new DialogCustomLineWidth.Callback() {
                    @Override
                    public void done(int lineWidth) {
                        mNoteManager.setStrokeWidth(lineWidth, mActionDoneCallback);
                    }
                });
                mNoteManager.post(event);
                break;
        }
    }

    @Override
    public void onRawTouchPointListReceived() {
        renderInBackground();
    }

    private void renderInBackground() {
        new RenderInBackgroundAction().execute(mNoteManager, null);
    }
}
