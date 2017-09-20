package com.onyx.edu.note.handler;

import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.api.event.DrawingTouchEvent;
import com.onyx.android.sdk.scribble.asyncrequest.shape.GetSelectedShapeListRequest;
import com.onyx.android.sdk.scribble.data.MirrorType;
import com.onyx.android.sdk.scribble.data.ScribbleMode;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.ui.data.MenuClickEvent;
import com.onyx.android.sdk.ui.data.MenuId;
import com.onyx.edu.note.actions.scribble.ChangeSelectedShapePositionAction;
import com.onyx.edu.note.actions.scribble.ChangeSelectedShapeRotationAction;
import com.onyx.edu.note.actions.scribble.ChangeSelectedShapeScaleAction;
import com.onyx.edu.note.actions.scribble.DocumentSaveAction;
import com.onyx.edu.note.actions.scribble.GetSelectedShapeListAction;
import com.onyx.edu.note.actions.scribble.GotoNextPageAction;
import com.onyx.edu.note.actions.scribble.GotoPrevPageAction;
import com.onyx.edu.note.actions.scribble.MirrorSelectedShapeAction;
import com.onyx.edu.note.actions.scribble.RedoAction;
import com.onyx.edu.note.actions.scribble.SelectShapeByPointListAction;
import com.onyx.edu.note.actions.scribble.ShapeSelectionAction;
import com.onyx.edu.note.actions.scribble.UndoAction;
import com.onyx.edu.note.data.ScribbleSubMenuID;
import com.onyx.edu.note.scribble.event.ChangeScribbleModeEvent;
import com.onyx.edu.note.scribble.event.GoToTargetPageEvent;
import com.onyx.edu.note.scribble.event.QuitScribbleEvent;
import com.onyx.edu.note.scribble.event.RequestInfoUpdateEvent;
import com.onyx.edu.note.scribble.event.ShowSubMenuEvent;
import com.onyx.edu.note.ui.HideSubMenuEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_SELECTOR;
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
 * Created by solskjaer49 on 2017/8/8 17:43.
 */

public class ShapeTransformHandler extends BaseHandler {

    private BaseCallback actionDoneCallback = new BaseCallback() {
        @Override
        public void done(BaseRequest request, Throwable e) {
            AsyncBaseNoteRequest noteRequest = (AsyncBaseNoteRequest)request;
            noteManager.post(new RequestInfoUpdateEvent(noteRequest.getShapeDataInfo(), request, e));
        }
    };

    private static final String TAG = ShapeTransformHandler.class.getSimpleName();
    private TouchPoint mShapeSelectStartPoint = null;
    private TouchPoint mShapeSelectPoint = null;
    private ControlMode currentControlMode = ControlMode.SelectMode;
    private TransformAction transformAction = TransformAction.Undefined;
    private TouchPointList shapeSelectPoints;
    private RectF selectedRectF;

    private enum ControlMode {SelectMode, OperatingMode}

    private enum TransformAction {Undefined, Zoom, Move, Rotation, XAxisMirror, YAxisMirror}

    public ShapeTransformHandler(NoteManager noteManager) {
        super(noteManager);
    }

    @Override
    public void onActivate(HandlerArgs args) {
        super.onActivate(args);
        noteManager.registerEventBus(this);
        currentControlMode = ControlMode.SelectMode;
        noteManager.getShapeDataInfo().setCurrentShapeType(SHAPE_SELECTOR);
        noteManager.sync(true, false);
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        noteManager.unregisterEventBus(this);
    }

    @Override
    public List<Integer> buildMainMenuIds() {
        List<Integer> functionMenuIds = new ArrayList<>();
        functionMenuIds.add(MenuId.SHAPE_SELECT);

        functionMenuIds.add(MenuId.PREV_PAGE);
        functionMenuIds.add(MenuId.NEXT_PAGE);
        functionMenuIds.add(MenuId.PAGE);
        return functionMenuIds;
    }

    @Override
    public List<Integer> buildToolBarMenuIds() {
        List<Integer> toolBarMenuIDList = new ArrayList<>();
        toolBarMenuIDList.add(MenuId.SCRIBBLE_TITLE);
        toolBarMenuIDList.add(MenuId.UNDO);
        toolBarMenuIDList.add(MenuId.REDO);
        return toolBarMenuIDList;
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
            case MenuId.PEN_STYLE:
            case MenuId.PEN_WIDTH:
            case MenuId.ERASER:
            case MenuId.BG:
                noteManager.post(new ShowSubMenuEvent(event.getMenuId()));
                break;
            case MenuId.SHAPE_SELECT:
                noteManager.post(new ChangeScribbleModeEvent(ScribbleMode.MODE_NORMAL_SCRIBBLE));
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

    private void redo() {
        noteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                RedoAction redoAction = new RedoAction(false);
                redoAction.execute(noteManager, actionDoneCallback);
            }
        });
    }

    private void undo() {
        noteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UndoAction undoAction = new UndoAction(false);
                undoAction.execute(noteManager, actionDoneCallback);
            }
        });
    }

    @Override
    public void handleSubMenuEvent(int subMenuID) {

    }

    @Override
    public void saveDocument(String uniqueID, String title, boolean closeAfterSave, BaseCallback callback) {
        DocumentSaveAction documentSaveAction = new DocumentSaveAction(uniqueID,
                title, closeAfterSave);
        documentSaveAction.execute(noteManager, callback);
    }

    @Override
    public void prevPage() {
        noteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GotoPrevPageAction prevPageAction = new GotoPrevPageAction();
                prevPageAction.execute(noteManager, actionDoneCallback);
            }
        });
    }

    @Override
    public void nextPage() {
        noteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GotoNextPageAction nextPageAction = new GotoNextPageAction();
                nextPageAction.execute(noteManager, actionDoneCallback);
            }
        });
    }

    @Override
    public void addPage() {

    }

    @Override
    public void deletePage() {

    }

    /**
     * When switch to shape transform handler.we handle touch event as next procedure.
     * When we receive touch event,detect if we already have select shape or not.
     * if does,we assume next touch event is going for control select shape.
     * if doesn't,we assume we are going to select shape.
     */
    private void onBeginShapeSelecting(final MotionEvent motionEvent) {
        Log.e(TAG, "onBeginShapeSelectEvent: ");
        shapeSelectPoints = new TouchPointList();
        new GetSelectedShapeListAction().execute(noteManager, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetSelectedShapeListRequest req = (GetSelectedShapeListRequest) request;
                if (req.getSelectedShapeList().size() > 0) {
                    currentControlMode = ControlMode.OperatingMode;
                    selectedRectF = req.getSelectedRectF();
                } else {
                    currentControlMode = ControlMode.SelectMode;
                }
            }
        });
        noteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                mShapeSelectStartPoint = new TouchPoint();
                mShapeSelectPoint = new TouchPoint();
            }
        });
    }

    private void detectTransformAction(RectF selectRect, MotionEvent event) {
        //TODO:dynamic detectionRange By selectRect width/height?
        final int detectionRange = 10;
        RectF leftTopCornerRect = new RectF(selectRect.left - detectionRange, selectRect.top - detectionRange,
                selectRect.left + detectionRange, selectRect.top + detectionRange);
        RectF rightTopCornerRect = new RectF(selectRect.right - detectionRange, selectRect.top - detectionRange,
                selectRect.right + detectionRange, selectRect.top + detectionRange);
        RectF leftBottomCornerRect = new RectF(selectRect.left - detectionRange, selectRect.bottom - detectionRange,
                selectRect.left + detectionRange, selectRect.bottom + detectionRange);
        RectF rightBottomCornerRect = new RectF(selectRect.right - detectionRange, selectRect.bottom - detectionRange,
                selectRect.right + detectionRange, selectRect.bottom + detectionRange);
        List<RectF> detectRectFList = new ArrayList<>();
        detectRectFList.add(leftTopCornerRect);
        detectRectFList.add(rightTopCornerRect);
        detectRectFList.add(leftBottomCornerRect);
        detectRectFList.add(rightBottomCornerRect);
        for (RectF rectF : detectRectFList) {
            if (rectF.contains(event.getX(), event.getY())) {
                transformAction = TransformAction.Zoom;
                return;
            }
        }
        transformAction = TransformAction.Move;
    }

    private void onShapeSelecting(MotionEvent motionEvent) {
        Log.e(TAG, "onShapeSelectingEvent: ");
        if (mShapeSelectStartPoint == null || mShapeSelectPoint == null) {
            return;
        }
        if (mShapeSelectStartPoint.x == 0 && mShapeSelectStartPoint.y == 0) {
            mShapeSelectStartPoint.x = motionEvent.getX();
            mShapeSelectStartPoint.y = motionEvent.getY();
        }
        mShapeSelectPoint.x = motionEvent.getX();
        mShapeSelectPoint.y = motionEvent.getY();
        switch (currentControlMode) {
            case SelectMode:
                new ShapeSelectionAction(mShapeSelectStartPoint, mShapeSelectPoint).execute(noteManager, null);
                break;
            case OperatingMode:
                if (transformAction == TransformAction.Undefined) {
                    detectTransformAction(selectedRectF, motionEvent);
                }
                switch (transformAction) {
                    case Move:
                        new ChangeSelectedShapePositionAction(mShapeSelectPoint, false).execute(noteManager, null);
                        break;
                    case Zoom:
                        new ChangeSelectedShapeScaleAction(mShapeSelectPoint, false).execute(noteManager, null);
                        break;
                    case Rotation:
                        new ChangeSelectedShapeRotationAction(mShapeSelectPoint, false).execute(noteManager, null);
                        break;
                }
                break;
        }

        if (shapeSelectPoints != null) {
            int n = motionEvent.getHistorySize();
            for(int i = 0; i < n; ++i) {
                shapeSelectPoints.add(TouchPoint.fromHistorical(motionEvent, i));
            }
            shapeSelectPoints.add(new TouchPoint(motionEvent.getX(), motionEvent.getY(),
                    motionEvent.getPressure(), motionEvent.getSize(), motionEvent.getEventTime()));
        }
    }

    //TODO:X/Y mirror is single tap function.no need to handle when get point move event.
    private void onFinishShapeSelecting() {
        Log.e(TAG, "onShapeSelectTouchPointListReceived: ");
        switch (currentControlMode) {
            case SelectMode:
                new SelectShapeByPointListAction(shapeSelectPoints).execute(noteManager, null);
                break;
            case OperatingMode:
                switch (transformAction) {
                    case Move:
                        new ChangeSelectedShapePositionAction(mShapeSelectPoint, true).execute(noteManager, null);
                        break;
                    case Zoom:
                        new ChangeSelectedShapeScaleAction(mShapeSelectPoint, true).execute(noteManager, null);
                        break;
                    case Rotation:
                        new ChangeSelectedShapeRotationAction(mShapeSelectPoint, true).execute(noteManager, null);
                        break;
                    case XAxisMirror:
                        new MirrorSelectedShapeAction(MirrorType.XAxisMirror,true).execute(noteManager,null);
                        break;
                    case YAxisMirror:
                        new MirrorSelectedShapeAction(MirrorType.YAxisMirror,true).execute(noteManager,null);
                        break;
                }
                selectedRectF = null;
                transformAction = TransformAction.Undefined;
                break;
        }
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

    private List<Integer> buildSubMenuBGIDList() {
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

    @Subscribe
    public void onDrawingTouchEvent(DrawingTouchEvent event) {
        forwardShapeSelecting(event.getMotionEvent());
    }

    private void forwardShapeSelecting(final MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            onBeginShapeSelecting(motionEvent);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            onShapeSelecting(motionEvent);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            onFinishShapeSelecting();
        }
    }

}
