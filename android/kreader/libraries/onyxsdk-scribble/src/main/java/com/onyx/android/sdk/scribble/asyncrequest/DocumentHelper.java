package com.onyx.android.sdk.scribble.asyncrequest;

import android.content.Context;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.data.NoteDocument;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.utils.InkUtils;

/**
 * Created by john on 12/8/2017.
 */

public class DocumentHelper {

    private NoteDocument noteDocument = new NoteDocument();

    public DocumentHelper() {
    }

    public void openDocument(final NoteManager noteManager, final Context context, final String documentUniqueId, final String parentUniqueId) {
        getNoteDocument().open(context, documentUniqueId, parentUniqueId);
        onDocumentOpened(noteManager);
    }

    public void createDocument(final NoteManager noteManager, final Context context, final String documentUniqueId, final String parentUniqueId) {
        getNoteDocument().create(context, documentUniqueId, parentUniqueId);
        onDocumentOpened(noteManager);
    }

    private void onDocumentOpened(final NoteManager noteManager) {
        noteManager.getRendererHelper().clearRenderBitmap();
        NoteModel.setDefaultEraserRadius(noteManager.getDeviceConfig().getEraserRadius());
        getNoteDocument().getNoteDrawingArgs().setEraserRadius(noteManager.getDeviceConfig().getEraserRadius());
        InkUtils.setPressureEntries(noteManager.getMappingConfig().getPressureList());
        EpdController.setStrokeWidth(getNoteDocument().getNoteDrawingArgs().strokeWidth);
        EpdController.setStrokeColor(getNoteDocument().getNoteDrawingArgs().strokeColor);
    }

    public void undo(final Context context, boolean inSpanLayoutMode) {
        getNoteDocument().getCurrentPage(context).undo(inSpanLayoutMode);
    }

    public void redo(final Context context, boolean inSpanLayoutMode) {
        getNoteDocument().getCurrentPage(context).redo(inSpanLayoutMode);
    }

    public void clearPageUndoRedo(final Context context) {
        NotePage currentPage = getNoteDocument().getCurrentPage(context);
        if (currentPage != null) {
            currentPage.clearUndoRedoRecord();
        }
    }

    public void save(final Context context, final String title , boolean closeAfterSave) {
        getNoteDocument().save(context, title);
        if (closeAfterSave) {
            getNoteDocument().close(context);
        }
    }

    public void setBackground(int bgType) {
        getNoteDocument().setBackground(bgType);
    }

    public void setBackgroundFilePath(String filePath){
        getNoteDocument().setBackgroundFilePath(filePath);
    }

    public void setLineLayoutBackground(int bgType) {
        getNoteDocument().setLineLayoutBackground(bgType);
    }

    public void setStrokeWidth(float width) {
        getNoteDocument().setStrokeWidth(width);
        EpdController.setStrokeWidth(width);
    }

    public NoteDrawingArgs getNoteDrawingArgs() {
        return getNoteDocument().getNoteDrawingArgs();
    }

    public float getStrokeWidth() {
        return getNoteDocument().getStrokeWidth();
    }

    public int getStrokeColor() {
        return getNoteDocument().getStrokeColor();
    }

    public int getLineLayoutBackground() {
        return getNoteDocument().getLineLayoutBackground();
    }

    public String getCurrentPageUniqueId() {
        return getNoteDocument().getCurrentPageUniqueId();
    }

    public void setStrokeColor(int color) {
        getNoteDocument().setStrokeColor(color);
        EpdController.setStrokeColor(color);
    }

    public void updateDrawingArgs(final NoteDrawingArgs drawingArgs) {
        setStrokeColor(drawingArgs.strokeColor);
        setStrokeWidth(drawingArgs.strokeWidth);
        setCurrentShapeType(drawingArgs.getCurrentShapeType());
        setBackground(drawingArgs.background);
    }

    public void updateShapeDataInfo(final Context context, final ShapeDataInfo shapeDataInfo) {
        shapeDataInfo.updateShapePageMap(
                getNoteDocument().getPageNameList(),
                getNoteDocument().getCurrentPageIndex());
        shapeDataInfo.setInUserErasing(inUserErasing());
        shapeDataInfo.updateDrawingArgs(getNoteDocument().getNoteDrawingArgs());
        shapeDataInfo.setCanRedoShape(getNoteDocument().getCurrentPage(context).canRedo());
        shapeDataInfo.setCanUndoShape(getNoteDocument().getCurrentPage(context).canUndo());
        shapeDataInfo.setDocumentUniqueId(getNoteDocument().getDocumentUniqueId());
    }

    public final NoteDocument getNoteDocument() {
        return noteDocument;
    }

    public NoteDrawingArgs.PenState getPenState() {
        return getNoteDocument().getPenState();
    }

    public void setPenState(NoteDrawingArgs.PenState penState) {
        getNoteDocument().setPenState(penState);
    }

    public boolean inShapeSelecting(){
        return getPenState() == NoteDrawingArgs.PenState.PEN_SHAPE_SELECTING;
    }

    public boolean inUserErasing() {
        return getPenState() == NoteDrawingArgs.PenState.PEN_USER_ERASING;
    }

    public int getCurrentShapeType() {
        return getNoteDocument().getNoteDrawingArgs().getCurrentShapeType();
    }

    public void setCurrentShapeType(int currentShapeType) {
        getNoteDocument().getNoteDrawingArgs().setCurrentShapeType(currentShapeType);
        updatePenStateByCurrentShapeType();
    }

    public void updatePenStateByCurrentShapeType() {
        int type = getCurrentShapeType();
        if (ShapeFactory.isDFBShape(type)) {
            setPenState(NoteDrawingArgs.PenState.PEN_SCREEN_DRAWING);
            return;
        }
        switch (type) {
            case ShapeFactory.SHAPE_ERASER:
                setPenState(NoteDrawingArgs.PenState.PEN_USER_ERASING);
                break;
            case ShapeFactory.SHAPE_SELECTOR:
                setPenState(NoteDrawingArgs.PenState.PEN_SHAPE_SELECTING);
                break;
            default:
                setPenState(NoteDrawingArgs.PenState.PEN_CANVAS_DRAWING);
                break;
        }
    }

}
