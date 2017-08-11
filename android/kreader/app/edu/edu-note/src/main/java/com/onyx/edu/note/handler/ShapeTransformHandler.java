package com.onyx.edu.note.handler;

import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginShapeSelectEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ShapeSelectTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ShapeSelectingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.shape.GetSelectedShapeListRequest;
import com.onyx.android.sdk.scribble.asyncrequest.shape.SelectShapeByPointListRequest;
import com.onyx.android.sdk.scribble.data.ScribbleMode;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.edu.note.actions.scribble.ChangeSelectedShapePositionAction;
import com.onyx.edu.note.actions.scribble.GetSelectedShapeListAction;
import com.onyx.edu.note.actions.scribble.SelectShapeByPointListAction;
import com.onyx.edu.note.actions.scribble.ShapeSelectionAction;
import com.onyx.edu.note.actions.scribble.ShapeShowTransformRectAction;
import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.edu.note.data.ScribbleToolBarMenuID;
import com.onyx.edu.note.scribble.event.ChangeScribbleModeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_PENCIL_SCRIBBLE;
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
    private static final String TAG = ShapeTransformHandler.class.getSimpleName();
    private TouchPoint mShapeSelectStartPoint = null;
    private TouchPoint mShapeSelectPoint = null;
    private ControlMode currentCortrolMode = ControlMode.SelectMode;

    private enum ControlMode {SelectMode, OperatingMode}

    public ShapeTransformHandler(NoteManager noteManager) {
        super(noteManager);
    }

    @Override
    public void onActivate() {
        super.onActivate();
        EventBus.getDefault().register(this);
        mNoteManager.getShapeDataInfo().setCurrentShapeType(SHAPE_SELECTOR);
        mNoteManager.sync(true, false);
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void buildFunctionBarMenuFunctionList() {
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
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.UNDO);
        mToolBarMenuFunctionIDList.add(ScribbleToolBarMenuID.REDO);
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
        //TODO:temp restore to normal scribble here.in shape select mode , may have different icon here.
        mNoteManager.getShapeDataInfo().setCurrentShapeType(SHAPE_PENCIL_SCRIBBLE);
        EventBus.getDefault().post(new ChangeScribbleModeEvent(ScribbleMode.MODE_NORMAL_SCRIBBLE));
    }

    @Override
    public void handleSubMenuFunction(int subMenuID) {

    }

    @Override
    public void handleToolBarMenuFunction(String uniqueID, String title, int toolBarMenuID) {

    }

    @Override
    public void saveDocument(String uniqueID, String title, boolean closeAfterSave, BaseCallback callback) {

    }

    @Override
    public void prevPage() {

    }

    @Override
    public void nextPage() {

    }

    @Override
    public void addPage() {

    }

    @Override
    public void deletePage() {

    }

    /**
     * TODO:test effect only.need design undo/redo scale/drag
     * When switch to shape transform handler.we handle touch event as next procedure.
     * When we receive touch event,detect if we already have select shape or not.
     * if does,we assume next touch event is going for control select shape.
     * if doesn't,we assume we are going to select shape.
     */
    @Subscribe
    public void onBeginShapeSelectEvent(BeginShapeSelectEvent event) {
        Log.e(TAG, "onBeginShapeSelectEvent: ");
        new GetSelectedShapeListAction().execute(mNoteManager, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetSelectedShapeListRequest req = (GetSelectedShapeListRequest) request;
                if (req.getSelectedShapeList().size() > 0) {
                    currentCortrolMode = ControlMode.OperatingMode;
                }
            }
        });
        mNoteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                mShapeSelectStartPoint = new TouchPoint();
                mShapeSelectPoint = new TouchPoint();
            }
        });
    }

    @Subscribe
    public void onShapeSelectingEvent(ShapeSelectingEvent event) {
        Log.e(TAG, "onShapeSelectingEvent: ");
        MotionEvent motionEvent = event.getMotionEvent();
        if (mShapeSelectStartPoint == null) {
            return;
        }
        if (mShapeSelectStartPoint.x == 0 && mShapeSelectStartPoint.y == 0) {
            mShapeSelectStartPoint.x = motionEvent.getX();
            mShapeSelectStartPoint.y = motionEvent.getY();
        }
        mShapeSelectPoint.x = motionEvent.getX();
        mShapeSelectPoint.y = motionEvent.getY();
        switch (currentCortrolMode) {
            case SelectMode:
                new ShapeSelectionAction(mShapeSelectStartPoint, mShapeSelectPoint).execute(mNoteManager, null);
                break;
            case OperatingMode:
                new ShapeShowTransformRectAction(mShapeSelectStartPoint, mShapeSelectPoint).execute(mNoteManager, null);
//                new ChangeSelectedShapePositionAction(mShapeSelectPoint).execute(mNoteManager, null);
                break;
        }
    }

    @Subscribe
    public void onShapeSelectTouchPointListReceived(ShapeSelectTouchPointListReceivedEvent event) {
        switch (currentCortrolMode) {
            case SelectMode:
                new SelectShapeByPointListAction(event.getTouchPointList()).execute(mNoteManager, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        SelectShapeByPointListRequest req = (SelectShapeByPointListRequest) request;
                        for (Shape shape : req.getSelectResultList()) {
                            Log.e(TAG, "shape:" + shape);
                        }
                    }
                });
                break;
            case OperatingMode:
                new ChangeSelectedShapePositionAction(mShapeSelectPoint).execute(mNoteManager, null);
                break;
        }
        mShapeSelectStartPoint = null;
        mShapeSelectPoint = null;
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
}
