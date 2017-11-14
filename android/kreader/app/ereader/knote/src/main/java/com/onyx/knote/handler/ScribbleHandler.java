package com.onyx.knote.handler;

import android.util.Log;
import android.util.SparseArray;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.ScribbleMode;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.ui.data.MenuClickEvent;
import com.onyx.android.sdk.ui.data.MenuId;
import com.onyx.android.sdk.ui.dialog.DialogCustomLineWidth;
import com.onyx.knote.actions.scribble.ClearAllFreeShapesAction;
import com.onyx.knote.actions.scribble.DocumentAddNewPageAction;
import com.onyx.knote.actions.scribble.DocumentDeletePageAction;
import com.onyx.knote.actions.scribble.DocumentSaveAction;
import com.onyx.knote.actions.scribble.GotoNextPageAction;
import com.onyx.knote.actions.scribble.GotoPrevPageAction;
import com.onyx.knote.actions.scribble.NoteBackgroundChangeAction;
import com.onyx.knote.actions.scribble.RedoAction;
import com.onyx.knote.actions.scribble.RenderInBackgroundAction;
import com.onyx.knote.actions.scribble.UndoAction;
import com.onyx.knote.data.ScribbleSubMenuID;
import com.onyx.knote.scribble.event.ChangeScribbleModeEvent;
import com.onyx.knote.scribble.event.CustomWidthEvent;
import com.onyx.knote.scribble.event.GoToTargetPageEvent;
import com.onyx.knote.scribble.event.QuitScribbleEvent;
import com.onyx.knote.scribble.event.RequestInfoUpdateEvent;
import com.onyx.knote.scribble.event.ShowSubMenuEvent;
import com.onyx.knote.ui.HideSubMenuEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_ERASER;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_PENCIL_SCRIBBLE;
import static com.onyx.knote.data.ScribbleSubMenuID.Background.BG_CALENDAR;
import static com.onyx.knote.data.ScribbleSubMenuID.Background.BG_EMPTY;
import static com.onyx.knote.data.ScribbleSubMenuID.Background.BG_ENGLISH;
import static com.onyx.knote.data.ScribbleSubMenuID.Background.BG_GRID;
import static com.onyx.knote.data.ScribbleSubMenuID.Background.BG_GRID_5_5;
import static com.onyx.knote.data.ScribbleSubMenuID.Background.BG_GRID_POINT;
import static com.onyx.knote.data.ScribbleSubMenuID.Background.BG_LEFT_GRID;
import static com.onyx.knote.data.ScribbleSubMenuID.Background.BG_LINE;
import static com.onyx.knote.data.ScribbleSubMenuID.Background.BG_LINE_1_6;
import static com.onyx.knote.data.ScribbleSubMenuID.Background.BG_LINE_2_0;
import static com.onyx.knote.data.ScribbleSubMenuID.Background.BG_LINE_COLUMN;
import static com.onyx.knote.data.ScribbleSubMenuID.Background.BG_MATS;
import static com.onyx.knote.data.ScribbleSubMenuID.Background.BG_MUSIC;
import static com.onyx.knote.data.ScribbleSubMenuID.Background.BG_TABLE_GRID;
import static com.onyx.knote.data.ScribbleSubMenuID.Eraser.ERASE_PARTIALLY;
import static com.onyx.knote.data.ScribbleSubMenuID.Eraser.ERASE_TOTALLY;
import static com.onyx.knote.data.ScribbleSubMenuID.PenStyle.BRUSH_PEN_STYLE;
import static com.onyx.knote.data.ScribbleSubMenuID.PenStyle.CIRCLE_STYLE;
import static com.onyx.knote.data.ScribbleSubMenuID.PenStyle.LINE_STYLE;
import static com.onyx.knote.data.ScribbleSubMenuID.PenStyle.NORMAL_PEN_STYLE;
import static com.onyx.knote.data.ScribbleSubMenuID.PenStyle.RECT_STYLE;
import static com.onyx.knote.data.ScribbleSubMenuID.PenStyle.TRIANGLE_45_STYLE;
import static com.onyx.knote.data.ScribbleSubMenuID.PenStyle.TRIANGLE_60_STYLE;
import static com.onyx.knote.data.ScribbleSubMenuID.PenStyle.TRIANGLE_90_STYLE;
import static com.onyx.knote.data.ScribbleSubMenuID.PenStyle.TRIANGLE_STYLE;
import static com.onyx.knote.data.ScribbleSubMenuID.Thickness.THICKNESS_BOLD;
import static com.onyx.knote.data.ScribbleSubMenuID.Thickness.THICKNESS_CUSTOM_BOLD;
import static com.onyx.knote.data.ScribbleSubMenuID.Thickness.THICKNESS_LIGHT;
import static com.onyx.knote.data.ScribbleSubMenuID.Thickness.THICKNESS_NORMAL;
import static com.onyx.knote.data.ScribbleSubMenuID.Thickness.THICKNESS_ULTRA_BOLD;
import static com.onyx.knote.data.ScribbleSubMenuID.Thickness.THICKNESS_ULTRA_LIGHT;

/**
 * Created by solskjaer49 on 2017/6/23 17:49.
 */

public class ScribbleHandler extends BaseHandler {
    private static final String TAG = ScribbleHandler.class.getSimpleName();

    public ScribbleHandler(NoteManager noteManager) {
        super(noteManager);
    }

    private BaseCallback mActionDoneCallback = new BaseCallback() {
        @Override
        public void done(BaseRequest request, Throwable e) {
            AsyncBaseNoteRequest noteRequest = (AsyncBaseNoteRequest)request;
            noteManager.post(new RequestInfoUpdateEvent(noteRequest.getShapeDataInfo(), request, e));
        }
    };


    @Override
    public void onActivate(HandlerArgs args) {
        super.onActivate(args);
        noteManager.registerEventBus(this);
        noteManager.getShapeDataInfo().setCurrentShapeType(SHAPE_PENCIL_SCRIBBLE);
        noteManager.sync(true, true);
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        noteManager.unregisterEventBus(this);
    }

    @Override
    public List<Integer> buildMainMenuIds() {
        List<Integer> functionMenuIds = new ArrayList<>();
        functionMenuIds.add(MenuId.PEN_STYLE);
        functionMenuIds.add(MenuId.BG);
        functionMenuIds.add(MenuId.ERASER);
        functionMenuIds.add(MenuId.PEN_WIDTH);
        functionMenuIds.add(MenuId.SHAPE_SELECT);

        functionMenuIds.add(MenuId.ADD_PAGE);
        functionMenuIds.add(MenuId.DELETE_PAGE);
        functionMenuIds.add(MenuId.PREV_PAGE);
        functionMenuIds.add(MenuId.NEXT_PAGE);
        functionMenuIds.add(MenuId.PAGE);

        return functionMenuIds;
    }

    @Override
    public List<Integer> buildToolBarMenuIds() {
        List<Integer> toolBarMenuIds = new ArrayList<>();
        toolBarMenuIds.add(MenuId.SCRIBBLE_TITLE);
        toolBarMenuIds.add(MenuId.SWITCH_TO_SPAN_SCRIBBLE_MODE);
        toolBarMenuIds.add(MenuId.UNDO);
        toolBarMenuIds.add(MenuId.SAVE);
        toolBarMenuIds.add(MenuId.REDO);
        toolBarMenuIds.add(MenuId.SETTING);
        toolBarMenuIds.add(MenuId.EXPORT);
        return toolBarMenuIds;
    }

    @Override
    public SparseArray<List<Integer>> buildSubMenuIds() {
        SparseArray<List<Integer>> functionBarSubMenuIDMap = new SparseArray<>();
        functionBarSubMenuIDMap.put(MenuId.PEN_WIDTH, buildSubMenuThicknessIDList());
        functionBarSubMenuIDMap.put(MenuId.BG, buildSubMenuBGIDList());
        functionBarSubMenuIDMap.put(MenuId.ERASER, buildSubMenuEraserIDList());
        functionBarSubMenuIDMap.put(MenuId.PEN_STYLE, buildSubMenuPenStyleIDList());
        return functionBarSubMenuIDMap;
    }

    @Subscribe
    public void onMenuClickEvent(MenuClickEvent event) {
        switch (event.getMenuId()) {
            case MenuId.ADD_PAGE:
                addPage();
                break;
            case MenuId.DELETE_PAGE:
                deletePage();
                break;
            case MenuId.PREV_PAGE:
                prevPage();
                break;
            case MenuId.NEXT_PAGE:
                nextPage();
                break;
            case MenuId.PAGE:
                noteManager.post(new GoToTargetPageEvent());
                break;
            case MenuId.SHAPE_SELECT:
                onSetShapeSelectModeChanged();
                break;
            case MenuId.PEN_STYLE:
            case MenuId.PEN_WIDTH:
            case MenuId.ERASER:
            case MenuId.BG:
                noteManager.post(new ShowSubMenuEvent(event.getMenuId()));
                break;
            case MenuId.SWITCH_TO_SPAN_SCRIBBLE_MODE:
                switchToSpanLayoutMode();
                break;
            case MenuId.EXPORT:
                break;
            case MenuId.UNDO:
                undo();
                break;
            case MenuId.REDO:
                redo();
                break;
            case MenuId.SAVE:
//                saveDocument(uniqueID, title, false, null);
                break;
            case MenuId.SETTING:
                break;
            case MenuId.SCRIBBLE_TITLE:
                noteManager.post(new QuitScribbleEvent());
                break;
        }
        if (ScribbleSubMenuID.isSubMenuId(event.getMenuId())) {
            handleSubMenuEvent(event.getMenuId());
            noteManager.post(new HideSubMenuEvent());
        }
    }

    @Override
    public void handleSubMenuEvent(int subMenuID) {
        Log.e(TAG, "handleSubMenuEvent: " + subMenuID);
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

    private void switchToSpanLayoutMode() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                noteManager.post(new ChangeScribbleModeEvent(ScribbleMode.MODE_SPAN_SCRIBBLE));
            }
        });
    }

    @Override
    public void prevPage() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GotoPrevPageAction prevPageAction = new GotoPrevPageAction();
                prevPageAction.execute(noteManager, mActionDoneCallback);
            }
        });
    }

    @Override
    public void nextPage() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GotoNextPageAction nextPageAction = new GotoNextPageAction();
                nextPageAction.execute(noteManager, mActionDoneCallback);
            }
        });
    }

    @Override
    public void addPage() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DocumentAddNewPageAction addNewPageAction = new DocumentAddNewPageAction();
                addNewPageAction.execute(noteManager, mActionDoneCallback);
            }
        });
    }

    @Override
    public void deletePage() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DocumentDeletePageAction deletePageAction = new DocumentDeletePageAction();
                deletePageAction.execute(noteManager, mActionDoneCallback);
            }
        });
    }

    private void redo() {
        noteManager.syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                RedoAction reDoAction = new RedoAction();
                reDoAction.execute(noteManager, mActionDoneCallback);
            }
        });
    }

    private void undo() {
        noteManager.syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UndoAction unDoAction = new UndoAction();
                unDoAction.execute(noteManager, mActionDoneCallback);
            }
        });
    }

    @Override
    public void saveDocument(String uniqueID, String title, boolean closeAfterSave, BaseCallback callback) {
        DocumentSaveAction documentSaveAction = new DocumentSaveAction(uniqueID,
                title, closeAfterSave);
        documentSaveAction.execute(noteManager, callback);
    }

    private List<Integer> buildSubMenuThicknessIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(THICKNESS_ULTRA_LIGHT);
        resultList.add(THICKNESS_LIGHT);
        resultList.add(THICKNESS_NORMAL);
        resultList.add(THICKNESS_BOLD);
        resultList.add(THICKNESS_ULTRA_BOLD);
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
        NoteBackgroundChangeAction changeBGAction = new NoteBackgroundChangeAction(bgType, !noteManager.inUserErasing());
        changeBGAction.execute(noteManager, mActionDoneCallback);
    }

    private void onShapeChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        int shapeType = ScribbleSubMenuID.shapeTypeFromMenuID(subMenuID);
        noteManager.getShapeDataInfo().setCurrentShapeType(shapeType);
        noteManager.sync(true, ShapeFactory.createShape(shapeType).supportDFB());
    }

    private void onSetShapeSelectModeChanged(){
        noteManager.post(new ChangeScribbleModeEvent(ScribbleMode.MODE_SHAPE_TRANSFORM));
    }

    private void onEraserChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        switch (subMenuID) {
            case ScribbleSubMenuID.Eraser.ERASE_PARTIALLY:
                noteManager.getShapeDataInfo().setCurrentShapeType(SHAPE_ERASER);
                noteManager.sync(true, false);
                break;
            case ScribbleSubMenuID.Eraser.ERASE_TOTALLY:
                new ClearAllFreeShapesAction().execute(noteManager, mActionDoneCallback);
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
                noteManager.setStrokeWidth(ScribbleSubMenuID.strokeWidthFromMenuId(subMenuID), mActionDoneCallback);
                break;
            case ScribbleSubMenuID.Thickness.THICKNESS_CUSTOM_BOLD:
                CustomWidthEvent event = new CustomWidthEvent(new DialogCustomLineWidth.Callback() {
                    @Override
                    public void done(int lineWidth) {
                        noteManager.setStrokeWidth(lineWidth, mActionDoneCallback);
                    }
                });
                noteManager.post(event);
                break;
        }
    }

    @Override
    public void onRawTouchPointListReceived() {
        renderInBackground();
    }

    private void renderInBackground() {
        new RenderInBackgroundAction().execute(noteManager, null);
    }
}
