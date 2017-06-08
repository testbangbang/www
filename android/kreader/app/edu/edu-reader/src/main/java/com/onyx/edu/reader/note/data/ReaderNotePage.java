package com.onyx.edu.reader.note.data;

import android.content.Context;
import android.graphics.Matrix;

import com.onyx.android.sdk.scribble.data.*;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;
import com.onyx.edu.reader.note.model.ReaderNoteShapeModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuzeng on 9/16/16.
 */
public class ReaderNotePage {

    private String documentUniqueId;
    private String pageUniqueId;
    private String subPageUniqueId;

    private List<Shape> shapeList = new ArrayList<>();
    private List<Shape> newAddedShapeList = new ArrayList<>();
    private List<Shape> removedShapeList = new ArrayList<>();

    private boolean loaded = false;
    public Matrix lastMatrix;
    private UndoRedoManager undoRedoManager = new UndoRedoManager();


    public static abstract class RenderCallback {
        public abstract boolean isRenderAbort();
    }

    public ReaderNotePage() {
    }

    public ReaderNotePage(final String docId, final String pageId, final String subPageId) {
        documentUniqueId = docId;
        pageUniqueId = pageId;
        subPageUniqueId = subPageId;
    }

    public final String getDocumentUniqueId() {
        return documentUniqueId;
    }

    public final String getPageUniqueId() {
        return pageUniqueId;
    }

    public final String getSubPageUniqueId() {
        return subPageUniqueId;
    }

    public void clear(boolean addToHistory) {
        if (shapeList.size() > 0 && addToHistory) {
            removedShapeList.addAll(shapeList);
            undoRedoManager.addToHistory(ShapeActions.removeShapeListAction(shapeList), false);
        }
        shapeList.clear();
        newAddedShapeList.clear();
    }

    public void addShapeFromModel(final Shape shape) {
        shapeList.add(shape);
    }

    public void addShape(final Shape shape, boolean addToHistory) {
        updateShape(shape);
        newAddedShapeList.add(shape);
        shapeList.add(shape);
        if (addToHistory) {
            undoRedoManager.addToHistory(ShapeActions.addShapeAction(shape), false);
        }
        if (removedShapeList.contains(shape)){
            removedShapeList.remove(shape);
        }
    }

    public void addShapeList(final List<Shape> shapes) {
        addShapeList(shapes, true);
    }

    public void addShapeList(final List<Shape> shapes, boolean addToHistory) {
        for (Shape shape : shapes) {
            addShape(shape, addToHistory);
        }
    }

    public void removeShapeList(final List<Shape> shapes, boolean addToHistory) {
        for (Shape shape : shapes) {
            removeShape(shape, addToHistory);
        }
    }

    public boolean canRedo() {
        return undoRedoManager.canRedo();
    }

    public void redo() {
        ReaderNoteUndoRedoManager.redo(this, undoRedoManager);
    }

    public boolean canUndo() {
        return undoRedoManager.canUndo();
    }

    public void undo() {
        ReaderNoteUndoRedoManager.undo(this, undoRedoManager);
    }

    private void updateShape(final Shape shape) {
        shape.setDocumentUniqueId(getDocumentUniqueId());
        shape.setPageUniqueId(getPageUniqueId());
        shape.setSubPageUniqueId(getSubPageUniqueId());
        shape.ensureShapeUniqueId();
        shape.updateBoundingRect();
    }

    public void removeShape(final Shape shape, boolean addToActionHistory) {
        updateShape(shape);
        removedShapeList.add(shape);
        newAddedShapeList.remove(shape);
        shapeList.remove(shape);
        if (addToActionHistory) {
            undoRedoManager.addToHistory(ShapeActions.removeShapeAction(shape), false);
        }
    }

    public void removeShapesByTouchPointList(final TouchPointList touchPointList, final float radius) {
        if (touchPointList == null) {
            return;
        }
        Map<String, Shape> hitShapes = new HashMap<>();
        for(Shape shape : shapeList) {
            for(TouchPoint touchPoint : touchPointList.getPoints()) {
                if (shape.fastHitTest(touchPoint.getX(), touchPoint.getY(), radius)) {
                    hitShapes.put(shape.getShapeUniqueId(), shape);
                    break;
                }
            }
        }

        for(Map.Entry<String, Shape> entry : hitShapes.entrySet()) {
            for(TouchPoint touchPoint : touchPointList.getPoints()) {
                if (entry.getValue().hitTest(touchPoint.getX(), touchPoint.getY(), radius)) {
                    removeShape(entry.getValue(), true);
                    break;
                }
            }
        }
    }

    public final List<Shape> getShapeList() {
        return shapeList;
    }

    public void render(final RenderContext renderContext, final RenderCallback callback) {
        if (shapeList == null) {
            return;
        }
        checkContextMatrix(renderContext);
        for(Shape shape : shapeList) {
            shape.render(renderContext);
            if (callback != null && callback.isRenderAbort()) {
                break;
            }
        }
    }

    private void checkContextMatrix(final RenderContext renderContext) {
        if (lastMatrix == null) {
            lastMatrix = new Matrix(renderContext.matrix);
            renderContext.force = true;
            return;
        }
        renderContext.force = !lastMatrix.equals(renderContext.matrix);
        lastMatrix.set(renderContext.matrix);
    }

    /**
     * return null if not exists
     * @param context
     * @param docUniqueId
     * @param pageName
     * @param subPageName
     * @return
     */
    public static final ReaderNotePage loadPage(final Context context, final String docUniqueId, final String pageName, final String subPageName) {
        final List<ShapeModel> list = ShapeDataProvider.loadShapeList(context, docUniqueId, pageName, subPageName);
        final ReaderNotePage notePage = createPage(context, docUniqueId, pageName, subPageName);
        for(ShapeModel model : list) {
            final Shape shape = ShapeFactory.shapeFromModel(model);
            notePage.addShapeFromModel(shape);
        }
        return notePage;
    }

    public void loadPage(final Context context) {
        newAddedShapeList.clear();
        removedShapeList.clear();
        final List<ReaderNoteShapeModel> modelList = ReaderNoteDataProvider.loadShapeList(context, getDocumentUniqueId(), getPageUniqueId(), getSubPageUniqueId());
        for(ReaderNoteShapeModel model : modelList) {
            addShapeFromModel(ReaderShapeFactory.shapeFromModel(model));
        }

        final List<ReaderFormShapeModel> formShapeModels = ReaderNoteDataProvider.loadFormShapeList(context, getDocumentUniqueId(), getPageUniqueId(), getSubPageUniqueId());
        for(ReaderFormShapeModel model : formShapeModels) {
            addShapeFromModel(ReaderShapeFactory.shapeFromFormModel(model));
        }
        setLoaded(true);
    }

    public static final ReaderNotePage createPage(final Context context, final String docUniqueId, final String pageName, final String subPageName) {
        final ReaderNotePage page = new ReaderNotePage(docUniqueId, pageName, subPageName);
        return page;
    }

    public List<ReaderNoteShapeModel> getNewAddedShapeModeList() {
        List<ReaderNoteShapeModel> modelList = new ArrayList<ReaderNoteShapeModel>(newAddedShapeList.size());
        for(Shape shape : newAddedShapeList) {
            final ReaderNoteShapeModel model = ReaderShapeFactory.modelFromShape(shape);
            modelList.add(model);
        }
        return modelList;
    }

    public List<Shape> getNewAddedShapeList() {
        return newAddedShapeList;
    }

    public List<String> getRemovedShapeIdList() {
        List<String> list = new ArrayList<>();
        for(Shape shape: removedShapeList) {
            list.add(shape.getShapeUniqueId());
        }
        return list;
    }

    public boolean savePageInBackground(final Context context) {
        return false;
    }

    public boolean savePage(final Context context) {
        List<ReaderNoteShapeModel> noteShapeModels = new ArrayList<>();

        for(Shape shape : newAddedShapeList) {
            final ReaderNoteShapeModel model = ReaderShapeFactory.modelFromShape(shape);
            noteShapeModels.add(model);
        }

        if (noteShapeModels.size() > 0) {
            ReaderNoteDataProvider.saveShapeList(context, noteShapeModels);
        }

        List<String> removeNoteShapes = new ArrayList<>();
        List<String> removeFormShapes = new ArrayList<>();
        for(Shape shape: removedShapeList) {
            if (shape.isFormShape()) {
                removeFormShapes.add(shape.getShapeUniqueId());
            }else {
                removeNoteShapes.add(shape.getShapeUniqueId());
            }

        }
        if (removeNoteShapes.size() > 0) {
            ReaderNoteDataProvider.removeShapesByIdList(context, removeNoteShapes);
        }
        if (removeFormShapes.size() > 0) {
            ReaderNoteDataProvider.removeFormShapesByIdList(context, removeFormShapes);
        }
        newAddedShapeList.clear();
        removedShapeList.clear();
        return true;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean l) {
        loaded = l;
    }

    public boolean hasShapes() {
        return shapeList != null && shapeList.size() > 0;
    }

    public boolean hasPendingShapes() {
        return newAddedShapeList.size() > 0 || removedShapeList.size() > 0;
    }

    public void remove() {

    }

}
