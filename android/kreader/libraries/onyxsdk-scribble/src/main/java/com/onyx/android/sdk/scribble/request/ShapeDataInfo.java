package com.onyx.android.sdk.scribble.request;

import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.PageNameList;

/**
 * Created by zhuzeng on 6/7/16.
 */
public class ShapeDataInfo {

    private volatile PageNameList pageNameList = new PageNameList();
    private int currentPageIndex;
    private boolean canUndoShape;
    private boolean canRedoShape;
    private boolean inUserErasing;
    private String documentUniqueId;
    private NoteDrawingArgs drawingArgs = new NoteDrawingArgs();


    public final PageNameList getPageNameList() {
        return pageNameList;
    }

    public boolean hasShapes() {
        return (pageNameList.size() > 0);
    }

    public void updateShapePageMap(final PageNameList src, int currentPage) {
        pageNameList.addAll(src.getPageNameList());
        currentPageIndex = currentPage;
    }

    public void setBackground(final int bg) {
        drawingArgs.background = bg;
    }

    public int getBackground() {
        return drawingArgs.background;
    }

    public void setLineLayoutBackground(final int bg) {
        drawingArgs.setLineLayoutBackground(bg);
    }

    public int getLineLayoutBackground() {
        return drawingArgs.getLineLayoutBackground();
    }

    public int getPageCount() {
        return pageNameList.size();
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public int getHumanReadableCurPageIndex(){
        if (getPageCount() == 0) {
            return 0;
        } else {
            return currentPageIndex + 1;
        }
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
}
