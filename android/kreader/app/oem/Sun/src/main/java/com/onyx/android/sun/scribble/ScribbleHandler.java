package com.onyx.android.sun.scribble;

import android.util.Log;
import android.util.SparseArray;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.ClearAllFreeShapesRequest;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.PageAddRequest;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.PageNextRequest;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.PagePrevRequest;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.PageRemoveRequest;
import com.onyx.android.sdk.scribble.asyncrequest.note.NoteDocumentSaveRequest;
import com.onyx.android.sdk.scribble.asyncrequest.shape.NoteBackgroundChangeRequest;
import com.onyx.android.sdk.scribble.asyncrequest.shape.RenderInBackgroundRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.utils.NoteViewUtil;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_ERASER;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_PENCIL_SCRIBBLE;


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
            AsyncBaseNoteRequest noteRequest = (AsyncBaseNoteRequest) request;
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
        return functionMenuIds;
    }

    @Override
    public List<Integer> buildToolBarMenuIds() {
        List<Integer> toolBarMenuIds = new ArrayList<>();
        return toolBarMenuIds;
    }

    @Override
    public SparseArray<List<Integer>> buildSubMenuIds() {
        SparseArray<List<Integer>> functionBarSubMenuIDMap = new SparseArray<>();
        return functionBarSubMenuIDMap;
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

    private void onBackgroundChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        int bgType = ScribbleSubMenuID.bgFromMenuID(subMenuID);
        noteManager.getShapeDataInfo().setBackground(bgType);
        NoteBackgroundChangeRequest bgChangeRequest = new NoteBackgroundChangeRequest(bgType, !noteManager.inUserErasing());
        bgChangeRequest.setRender(true);
        noteManager.submitRequest(bgChangeRequest, mActionDoneCallback);
    }

    private void onShapeChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        int shapeType = ScribbleSubMenuID.shapeTypeFromMenuID(subMenuID);
        noteManager.getShapeDataInfo().setCurrentShapeType(shapeType);
        noteManager.sync(true, ShapeFactory.createShape(shapeType).supportDFB());
    }

    @Override
    public void prevPage() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                PagePrevRequest prevRequest = new PagePrevRequest();
                prevRequest.setRender(true);
                noteManager.submitRequest(prevRequest, mActionDoneCallback);
            }
        });
    }

    @Override
    public void nextPage() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                PageNextRequest nextRequest = new PageNextRequest();
                nextRequest.setRender(true);
                noteManager.submitRequest(nextRequest, mActionDoneCallback);
            }
        });
    }

    @Override
    public void addPage() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                PageAddRequest pageAddRequest = new PageAddRequest(-1);
                pageAddRequest.setRender(true);
                noteManager.submitRequest(pageAddRequest, mActionDoneCallback);
            }
        });
    }

    @Override
    public void deletePage() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                PageRemoveRequest pageRemoveRequest = new PageRemoveRequest();
                pageRemoveRequest.setRender(true);
                noteManager.submitRequest(pageRemoveRequest, mActionDoneCallback);
            }
        });
    }

    @Override
    public void saveDocument(String uniqueID, String title, boolean closeAfterSave, BaseCallback callback) {
        NoteDocumentSaveRequest saveRequest = new NoteDocumentSaveRequest(title, closeAfterSave);
        if (!closeAfterSave) {
            saveRequest.setRender(true);
        }
        noteManager.submitRequestWithIdentifier(saveRequest, uniqueID, callback);
    }

    @Override
    public void onRawTouchPointListReceived() {
        renderInBackground();
    }

    private void renderInBackground() {
        List<Shape> shapes = noteManager.detachStash();
        if (CollectionUtils.isNullOrEmpty(shapes)) {
            return;
        }
        RenderInBackgroundRequest request = new RenderInBackgroundRequest(shapes);
        noteManager.submitRequest(request, null);
    }

    public void onEraserChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        switch (subMenuID) {
            case ScribbleSubMenuID.Eraser.ERASE_PARTIALLY:
                noteManager.getShapeDataInfo().setCurrentShapeType(SHAPE_ERASER);
                noteManager.sync(true, false);
                break;
            case ScribbleSubMenuID.Eraser.ERASE_TOTALLY:
                ClearAllFreeShapesRequest clearAllFreeShapesRequest = new ClearAllFreeShapesRequest();
                NoteViewUtil.setFullUpdate(true);
                noteManager.submitRequest(clearAllFreeShapesRequest, mActionDoneCallback);
                break;
        }
    }

    public void onStrokeWidthChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        switch (subMenuID) {
            case ScribbleSubMenuID.Thickness.THICKNESS_ULTRA_LIGHT:
            case ScribbleSubMenuID.Thickness.THICKNESS_LIGHT:
            case ScribbleSubMenuID.Thickness.THICKNESS_NORMAL:
            case ScribbleSubMenuID.Thickness.THICKNESS_BOLD:
            case ScribbleSubMenuID.Thickness.THICKNESS_ULTRA_BOLD:
                noteManager.setStrokeWidth(ScribbleSubMenuID.strokeWidthFromMenuId(subMenuID), mActionDoneCallback);
                break;
            case ScribbleSubMenuID.Thickness.THICKNESS_CUSTOM_BOLD:
                noteManager.setStrokeWidth(3.0f, mActionDoneCallback);
                break;
        }
    }
}
