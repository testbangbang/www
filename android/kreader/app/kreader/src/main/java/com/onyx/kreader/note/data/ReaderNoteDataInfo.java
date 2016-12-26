package com.onyx.kreader.note.data;

import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;

/**
 * Created by zhuzeng on 9/16/16.
 */
public class ReaderNoteDataInfo {

    private volatile ReaderNotePageNameMap pageMap = new ReaderNotePageNameMap();
    private boolean canUndoShape;
    private boolean canRedoShape;
    private boolean inUserErasing;
    private volatile boolean contentRendered;
    private String documentUniqueId;
    private NoteDrawingArgs drawingArgs = new NoteDrawingArgs();
    private boolean requestFinished = false;

    public final ReaderNotePageNameMap getPageMap() {
        return pageMap;
    }

    public boolean hasShapes() {
        return (pageMap.size() > 0);
    }

    public void setBackground(final int bg) {
        drawingArgs.background = bg;
    }

    public int getBackground() {
        return drawingArgs.background;
    }

    public int getPageCount() {
        return pageMap.size();
    }

    public boolean isCanUndoShape() {
        return canUndoShape;
    }

    public void setCanUndoShape(boolean canUndoShape) {
        this.canUndoShape = canUndoShape;
    }

    public boolean isCanRedoShape() {
        return canRedoShape;
    }

    public void setCanRedoShape(boolean canRedoShape) {
        this.canRedoShape = canRedoShape;
    }

    public float getEraserRadius() {
        return drawingArgs.eraserRadius;
    }

    public void setEraserRadius(float eraserRadius) {
        drawingArgs.eraserRadius = eraserRadius;
    }

    public float getStrokeWidth() {
        return drawingArgs.strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        drawingArgs.strokeWidth = strokeWidth;
    }

    public int getStrokeColor() {
        return drawingArgs.strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        drawingArgs.strokeColor = strokeColor;
    }

    public boolean isInUserErasing() {
        return inUserErasing;
    }

    public void setInUserErasing(boolean inUserErasing) {
        this.inUserErasing = inUserErasing;
    }

    public int getCurrentShapeType() {
        return drawingArgs.getCurrentShapeType();
    }

    public void setCurrentShapeType(int currentShape) {
        drawingArgs.setCurrentShapeType(currentShape);
    }

    public String getDocumentUniqueId() {
        return documentUniqueId;
    }

    public void setDocumentUniqueId(String documentUniqueId) {
        this.documentUniqueId = documentUniqueId;
    }

    public NoteDrawingArgs getDrawingArgs() {
        return drawingArgs;
    }

    public void updateDrawingArgs(NoteDrawingArgs other) {
        drawingArgs.copyFrom(other);
    }

    public boolean isContentRendered() {
        return contentRendered;
    }

    public void setContentRendered(boolean contentRendered) {
        this.contentRendered = contentRendered;
    }

    public boolean isRequestFinished() {
        return requestFinished;
    }

    public void setRequestFinished(boolean requestFinished) {
        this.requestFinished = requestFinished;
    }
}
