package com.onyx.knote.handler;

import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
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
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.knote.NoteApplication;
import com.onyx.knote.actions.scribble.ClearAllFreeShapesAction;
import com.onyx.knote.actions.scribble.ExportEditedPicAction;
import com.onyx.knote.actions.scribble.RedoAction;
import com.onyx.knote.actions.scribble.RenderInBackgroundAction;
import com.onyx.knote.actions.scribble.SetBackgroundAsLocalFileAction;
import com.onyx.knote.actions.scribble.UndoAction;
import com.onyx.knote.data.ScribbleSubMenuID;
import com.onyx.knote.scribble.event.ChangeScribbleModeEvent;
import com.onyx.knote.scribble.event.CustomWidthEvent;
import com.onyx.knote.scribble.event.QuitScribbleEvent;
import com.onyx.knote.scribble.event.RequestInfoUpdateEvent;
import com.onyx.knote.scribble.event.ShowSubMenuEvent;
import com.onyx.knote.ui.HideSubMenuEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_ERASER;
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
import static com.onyx.knote.data.ScribbleSubMenuID.Thickness.THICKNESS_ULTRA_LIGHT;

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

    private Uri editUri;
    public PicEditHandler(NoteManager mNoteManager) {
        super(mNoteManager);
    }

    @Override
    public void onActivate(HandlerArgs args) {
        super.onActivate(args);
        noteManager.registerEventBus(this);
        noteManager.sync(true, true);
        editUri = args.getEditPicUri();
        final String path = FileUtils.getRealFilePathFromUri(NoteApplication.getInstance(), editUri);
        SetBackgroundAsLocalFileAction action = new SetBackgroundAsLocalFileAction(
                path, !noteManager.inUserErasing());
        action.execute(noteManager, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                AsyncBaseNoteRequest noteRequest = (AsyncBaseNoteRequest) request;
                noteManager.post(new RequestInfoUpdateEvent(noteRequest.getShapeDataInfo(), request, e));
                noteManager.setLimitRect(getPicCustomLimitRect(path));
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
    public List<Integer> buildMainMenuIds() {
        List<Integer> functionBarMenuIDList = new ArrayList<>();
        functionBarMenuIDList.add(MenuId.PEN_STYLE);
        functionBarMenuIDList.add(MenuId.ERASER);
        functionBarMenuIDList.add(MenuId.PEN_WIDTH);
        return functionBarMenuIDList;
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
        functionBarSubMenuIDMap.put(MenuId.ERASER, buildSubMenuEraserIDList());
        functionBarSubMenuIDMap.put(MenuId.PEN_STYLE, buildSubMenuPenStyleIDList());
        return functionBarSubMenuIDMap;
    }

    @Subscribe
    public void onMenuClickEvent(MenuClickEvent event) {
        switch (event.getMenuId()) {
            case MenuId.SHAPE_SELECT:
                onSetShapeSelectModeChanged();
                break;
            case MenuId.PEN_STYLE:
            case MenuId.PEN_WIDTH:
            case MenuId.ERASER:
                noteManager.post(new ShowSubMenuEvent(event.getMenuId()));
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
            case MenuId.SWITCH_TO_SPAN_SCRIBBLE_MODE:
                switchToSpanLayoutMode();
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
    public void saveDocument(String uniqueID, String title, final boolean closeAfterSave, final BaseCallback callback) {
        noteManager.syncWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                new ExportEditedPicAction(noteManager.getHostView().getContext(), noteManager.getShapeDataInfo().getDocumentUniqueId(),
                        noteManager.getShapeDataInfo().getPageNameList().getPageNameList().get(0), editUri)
                        .execute(noteManager, new BaseCallback() {
                            @Override
                            public void done(BaseRequest request, Throwable e) {
                                noteManager.syncWithCallback(false, !closeAfterSave, callback);
                            }
                        });
            }
        });
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
