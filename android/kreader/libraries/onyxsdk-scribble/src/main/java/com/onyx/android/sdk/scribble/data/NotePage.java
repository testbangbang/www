package com.onyx.android.sdk.scribble.data;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import com.onyx.android.sdk.scribble.shape.*;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 4/23/16.
 * Manager for a list of shapes in single page.
 * To make it easy for activity or other class to manage shapes.
 * ShapeManager->NotePage->Shape->ShapeModel
 */
public class NotePage {

    private String documentUniqueId;
    private String pageUniqueId;
    private String subPageName;

    private List<Shape> shapeList = new ArrayList<Shape>();
    private List<Shape> newAddedShapeList = new ArrayList<Shape>();
    private List<Shape> removedShapeList = new ArrayList<Shape>();

    private int currentShapeType;
    private Shape currentShape;
    private boolean addToActionHistory = true;
    private UndoRedoManager undoRedoManager = new UndoRedoManager();


    public static abstract class RenderCallback {
        public abstract boolean isRenderAbort();
    }

    public NotePage() {
    }

    public NotePage(final String docId, final String pageId, final String spn) {
        documentUniqueId = docId;
        pageUniqueId = pageId;
        subPageName = spn;
    }

    public final String getDocumentUniqueId() {
        return documentUniqueId;
    }

    public final String getPageUniqueId() {
        return pageUniqueId;
    }

    public final String getSubPageName() {
        return subPageName;
    }

    public boolean isAddToActionHistory() {
        return addToActionHistory;
    }

    public void setAddToActionHistory(boolean add) {
        addToActionHistory = add;
    }

    public void clear() {
        shapeList.clear();
        newAddedShapeList.clear();
        removedShapeList.clear();
        undoRedoManager.clear();
    }

    public void addShapeFromModel(final Shape shape) {
        shapeList.add(shape);
    }

    public void addShape(final Shape shape) {
        updateShape(shape);
        newAddedShapeList.add(shape);
        if (isAddToActionHistory()) {
            undoRedoManager.addToHistory(ShapeActions.addShapeAction(shape), false);
        }
    }

    public void addShapeList(final List<Shape> shapes) {
        for(Shape shape : shapes) {
            updateShape(shape);
            newAddedShapeList.add(shape);
        }
        shapeList.addAll(shapes);
    }

    private void updateShape(final Shape shape) {
        shape.setDocumentUniqueId(getDocumentUniqueId());
        shape.setPageUniqueId(getPageUniqueId());
        shape.ensureShapeUniqueId();
    }

    public void removeShape(final Shape shape) {
        updateShape(shape);
        removedShapeList.add(shape);
        newAddedShapeList.remove(shape);
        shapeList.remove(shape);
        if (isAddToActionHistory()) {
            undoRedoManager.addToHistory(ShapeActions.removeShapeAction(shape), false);
        }
    }

    public final List<Shape> getShapeList() {
        return shapeList;
    }

    public final List<Shape> deatchShapeList() {
        final List<Shape> list = shapeList;
        shapeList = null;
        return list;
    }

    public void render(final Canvas canvas, final Paint paint, final Matrix matrix, final RenderCallback callback) {
        if (shapeList == null) {
            return;
        }
        for(Shape shape : shapeList) {
            shape.render(canvas, paint, matrix);
            if (callback != null && callback.isRenderAbort()) {
                break;
            }
        }
    }

    public void prepareShapePool(int shapeType) {
        currentShapeType = shapeType;
    }

    // create a new shape if not exist and make it as current shape.
    public final Shape getShapeFromPool() {
        switch (currentShapeType) {
            case ShapeFactory.SHAPE_NORMAL_SCRIBBLE:
                currentShape = new NormalScribbleShape();
                break;
            case ShapeFactory.SHAPE_CIRCLE:
                currentShape = new CircleShape();
                break;
            case ShapeFactory.SHAPE_RECTANGLE:
                currentShape = new RectangleShape();
                break;
            case ShapeFactory.SHAPE_TEXT:
                currentShape = new TexShape();
                break;
            case ShapeFactory.SHAPE_VARY_SCRIBBLE:
                currentShape = new BrushScribbleShape();
                break;
        }
        return currentShape;
    }

    public final Shape getCurrentShape() {
        return currentShape;
    }

    /**
     * return null if not exists
     * @param context
     * @param docUniqueId
     * @param pageName
     * @param subPageName
     * @return
     */
    public static final NotePage loadPage(final Context context, final String docUniqueId, final String pageName, final String subPageName) {
        final List<ShapeModel> list = ShapeDataProvider.loadShapeList(context, docUniqueId, pageName, subPageName);
        final NotePage notePage = createPage(context, docUniqueId, pageName, subPageName);
        for(ShapeModel model : list) {
            final Shape shape = ShapeFactory.shapeFromModel(model);
            notePage.addShapeFromModel(shape);
        }
        return notePage;
    }

    public void loadPage(final Context context) {
        final List<ShapeModel> modelList = ShapeDataProvider.loadShapeList(context, getDocumentUniqueId(), getPageUniqueId(), getSubPageName());
        for(ShapeModel model : modelList) {
            addShapeFromModel(ShapeFactory.shapeFromModel(model));
        }
    }

    public static final NotePage createPage(final Context context, final String docUniqueId, final String pageName, final String subPageName) {
        final NotePage page = new NotePage(docUniqueId, pageName, subPageName);
        return page;
    }

    /**
     * save new added shapes and remove shapes has been removed.
     * @return
     */
    public boolean savePage(final Context context) {
        List<ShapeModel> modelList = new ArrayList<ShapeModel>(newAddedShapeList.size());
        for(Shape shape : newAddedShapeList) {
            final ShapeModel model = ShapeFactory.modelFromShape(shape);
            modelList.add(model);
        }
        if (modelList.size() > 0) {
            ShapeDataProvider.saveShapeList(context, modelList);
        }

        for(Shape shape: removedShapeList) {
            ShapeDataProvider.removeShape(context, shape.getShapeUniqueId());
        }
        return true;
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
