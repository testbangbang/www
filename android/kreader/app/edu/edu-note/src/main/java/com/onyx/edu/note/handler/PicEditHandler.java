package com.onyx.edu.note.handler;

import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.ScribbleMode;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.ui.dialog.DialogCustomLineWidth;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.edu.note.NoteApplication;
import com.onyx.edu.note.actions.scribble.ClearAllFreeShapesAction;
import com.onyx.edu.note.actions.scribble.DocumentSaveAction;
import com.onyx.edu.note.actions.scribble.RedoAction;
import com.onyx.edu.note.actions.scribble.RenderInBackgroundAction;
import com.onyx.edu.note.actions.scribble.SetBackgroundAsLocalFileAction;
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

public class PicEditHandler extends BaseHandler {
    private static final String TAG = PicEditHandler.class.getSimpleName();
    private BaseCallback mActionDoneCallback = new BaseCallback() {
        @Override
        public void done(BaseRequest request, Throwable e) {
            AsyncBaseNoteRequest noteRequest = (AsyncBaseNoteRequest) request;
            noteManager.post(new RequestInfoUpdateEvent(noteRequest.getShapeDataInfo(), request, e));
        }
    };

    public PicEditHandler(NoteManager mNoteManager) {
        super(mNoteManager);
    }

    @Override
    public void onActivate(HandlerArgs args) {
        super.onActivate(args);
        noteManager.registerEventBus(this);
        noteManager.sync(true, true);
        final String path = FileUtils.getRealFilePathFromUri(NoteApplication.getInstance(), args.getEditPicUri());
        SetBackgroundAsLocalFileAction action = new SetBackgroundAsLocalFileAction(
                path, !noteManager.inUserErasing());
        action.execute(noteManager, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                AsyncBaseNoteRequest noteRequest = (AsyncBaseNoteRequest) request;
                noteManager.post(new RequestInfoUpdateEvent(noteRequest.getShapeDataInfo(), request, e));
                noteManager.setCustomLimitRect(getPicCustomLimitRect(path));
            }
        });
    }

    private Rect getPicCustomLimitRect(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return BitmapUtils.getScaleInSideAndCenterRect(
                noteManager.getHostView().getHeight(), noteManager.getHostView().getWidth(),
                options.outHeight, options.outWidth, false);
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        noteManager.unregisterEventBus(this);
    }

    @Override
    public void buildFunctionBarMenuFunctionList() {
        functionBarMenuIDList = new ArrayList<>();
        functionBarMenuIDList.add(ScribbleFunctionBarMenuID.PEN_STYLE);
        functionBarMenuIDList.add(ScribbleFunctionBarMenuID.ERASER);
        functionBarMenuIDList.add(ScribbleFunctionBarMenuID.PEN_WIDTH);
    }

    @Override
    protected void buildToolBarMenuFunctionList() {
        toolBarMenuIDList = new ArrayList<>();
        toolBarMenuIDList.add(ScribbleToolBarMenuID.UNDO);
        toolBarMenuIDList.add(ScribbleToolBarMenuID.REDO);
    }

    @Override
    protected void buildFunctionBarMenuSubMenuIDListSparseArray() {
        functionBarSubMenuIDMap = new SparseArray<>();
        functionBarSubMenuIDMap.put(ScribbleFunctionBarMenuID.PEN_WIDTH, buildSubMenuThicknessIDList());
        functionBarSubMenuIDMap.put(ScribbleFunctionBarMenuID.ERASER, buildSubMenuEraserIDList());
        functionBarSubMenuIDMap.put(ScribbleFunctionBarMenuID.PEN_STYLE, buildSubMenuPenStyleIDList());
    }

    @Override
    public void handleFunctionBarMenuFunction(int functionBarMenuID) {
        switch (functionBarMenuID) {
            case ScribbleFunctionBarMenuID.SHAPE_SELECT:
                onSetShapeSelectModeChanged();
                break;
            default:
                noteManager.post(new ShowSubMenuEvent(functionBarMenuID));
                break;
        }
    }

    @Override
    public void handleSubMenuFunction(int subMenuID) {
        Log.e(TAG, "handleSubMenuFunction: " + subMenuID);
        if (ScribbleSubMenuID.isThicknessGroup(subMenuID)) {
            onStrokeWidthChanged(subMenuID);
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
                switchToSpanLayoutMode();
                break;
            case ScribbleToolBarMenuID.UNDO:
                undo();
                break;
            case ScribbleToolBarMenuID.REDO:
                redo();
                break;
            case ScribbleToolBarMenuID.SAVE:
                saveDocument(uniqueID, title, false, null);
                break;
            case ScribbleToolBarMenuID.SETTING:
                break;
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
        resultList.add(THICKNESS_CUSTOM_BOLD);
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

    private void onShapeChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        int shapeType = ScribbleSubMenuID.shapeTypeFromMenuID(subMenuID);
        noteManager.getShapeDataInfo().setCurrentShapeType(shapeType);
        noteManager.sync(true, ShapeFactory.createShape(shapeType).supportDFB());
    }

    private void onSetShapeSelectModeChanged() {
        Log.e(TAG, "onSetShapeSelectModeChanged: ");
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
