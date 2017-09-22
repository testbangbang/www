package com.onyx.android.sdk.scribble.data;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;

import com.onyx.android.sdk.scribble.shape.BaseShape;
import com.onyx.android.sdk.scribble.shape.EPDShape;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.utils.MathUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuzeng on 4/23/16.
 * Manager for a list of shapes in single page.
 * To make it easy for activity or other class to manage shapes.
 * ShapeManager->NotePage->Shape->ShapeModel
 *
 * ShapeTransform Logic:
 * both shape and selected rectangle need transform.
 */
public class NotePage {

    private String documentUniqueId;
    private String pageUniqueId;
    private String subPageName;

    private List<Shape> shapeList = new ArrayList<>();
    private List<Shape> newAddedShapeList = new ArrayList<>();
    private List<Shape> removedShapeList = new ArrayList<>();
    private List<Shape> selectedShapeList = new ArrayList<>();
    private List<Shape> dirtyShapeList = new ArrayList<>();
    private List<Shape> preTransformShapeList = new ArrayList<>();

    private int currentShapeType;
    private Shape currentShape;
    private boolean loaded = false;
    private UndoRedoManager undoRedoManager = new UndoRedoManager();
    private PointF rotateCacheRectTopLeft, rotateCacheRectTopRight;
    private volatile SelectedRectF currentSelectedRectF = new SelectedRectF(new RectF());

    public void saveCurrentSelectShape() {
        if (preTransformShapeList.size() == 0) {
            deepCopyShapeList(selectedShapeList, preTransformShapeList);
        }
    }

    public void saveCurrentSelectPointRotationInitialData(){
        rotateCacheRectTopLeft = new PointF(getSelectedRect().getRectF().left, getSelectedRect().getRectF().top);
        rotateCacheRectTopRight = new PointF(getSelectedRect().getRectF().right, getSelectedRect().getRectF().top);
    }

    private void deepCopyShapeList(List<Shape> sourceList, List<Shape> destinationList) {
        for (Shape shape : sourceList) {
            Shape newShape = ShapeFactory.shapeFromModel(ShapeFactory.modelFromShape(shape));
            //TODO: we need deep copy here.
            newShape.getPoints().getPoints().clear();
            for (TouchPoint point : shape.getPoints().getPoints()) {
                try {
                    newShape.getPoints().getPoints().add(point.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
            destinationList.add(newShape);
        }
    }

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

    public void clearFreeShapes(boolean addToHistory) {
        List<Shape> freeShapes = getFreeShapes(shapeList);
        if (freeShapes.size() > 0 && addToHistory) {
            removedShapeList.addAll(freeShapes);
            undoRedoManager.addToHistory(ShapeActions.removeShapeListAction(freeShapes), false);
        }
        shapeList.removeAll(freeShapes);
        newAddedShapeList.removeAll(freeShapes);
    }

    private List<Shape> getFreeShapes(List<Shape> shapes) {
        List<Shape> freeShapes = new ArrayList<>();
        for (Shape shape : shapes) {
            if (shape.isFreePosition()) {
                freeShapes.add(shape);
            }
        }
        return freeShapes;
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
        addShapeList(shapes,true);
    }

    public void addShapeList(final List<Shape> shapes, boolean addToHistory) {
        for (Shape shape : shapes) {
            addShape(shape, addToHistory);
        }
    }

    public void removeTransformShape(final Shape shape) {
        updateShape(shape);
        dirtyShapeList.remove(shape);
        shapeList.remove(shape);
        if (selectedShapeList.contains(shape)){
            selectedShapeList.remove(shape);
        }
    }

    public void addTransformShape(final Shape shape) {
        shape.setSelected(true);
        updateShape(shape);
        dirtyShapeList.add(shape);
        shapeList.add(shape);
        selectedShapeList.add(shape);
        if (removedShapeList.contains(shape)) {
            removedShapeList.remove(shape);
        }
    }

    public void addTransformShapeList(final List<Shape> shapes) {
        for (Shape shape : shapes) {
            addTransformShape(shape);
        }
    }

    public void removeTransformShapeList(final List<Shape> shapes) {
        for (Shape shape : shapes) {
            removeTransformShape(shape);
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

    public void redo(final boolean isLineLayoutMode) {
        NotePageUndoRedoManager.redo(this, undoRedoManager, isLineLayoutMode);
    }

    public boolean canUndo() {
        return undoRedoManager.canUndo();
    }

    public void undo(final boolean isLineLayoutMode) {
        NotePageUndoRedoManager.undo(this, undoRedoManager, isLineLayoutMode);
    }

    public void clearUndoRedoRecord() {
        undoRedoManager.clear();
    }

    public void clearShapeSelectRecord() {
        for (Shape shape : selectedShapeList) {
            shape.setSelected(false);
            shape.setSelectRectOrientation(0);
        }
        clearSelectedRect();
        selectedShapeList.clear();
    }

    private void updateShape(final Shape shape) {
        shape.setDocumentUniqueId(getDocumentUniqueId());
        shape.setPageUniqueId(getPageUniqueId());
        shape.setSubPageUniqueId(getSubPageName());
        shape.ensureShapeUniqueId();
        shape.updateBoundingRect();
    }

    public void removeShape(final Shape shape, boolean addToActionHistory) {
        updateShape(shape);
        removedShapeList.add(shape);
        newAddedShapeList.remove(shape);
        dirtyShapeList.remove(shape);
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
            if (!shape.isFreePosition()) {
                continue;
            }
            for(TouchPoint touchPoint : touchPointList.getPoints()) {
                if (shape.fastHitTest(touchPoint.getX(), touchPoint.getY(), radius)) {
                    hitShapes.put(shape.getShapeUniqueId(), shape);
                    break;
                }
            }
        }

        for(Map.Entry<String, Shape> entry : hitShapes.entrySet()) {
            hitTestAndRemoveShape(entry, touchPointList, radius);
        }
    }

    @Nullable
    public List<Shape> selectShapeByTouchPointList(TouchPointList touchPointList, float radius) {
        if (touchPointList == null) {
            return null;
        }
        Map<String, Shape> hitShapes = new HashMap<>();
        for (Shape shape : shapeList) {
            if (!shape.isFreePosition()) {
                continue;
            }
            for (TouchPoint touchPoint : touchPointList.getPoints()) {
                if (shape.fastHitTest(touchPoint.getX(), touchPoint.getY(), radius)) {
                    hitShapes.put(shape.getShapeUniqueId(), shape);
                    break;
                }
            }
        }
        HashSet<Shape> shapeHashSet = new HashSet<>();
        List<Shape> resultList = new ArrayList<>();
        selectedShapeList.clear();
        for (Map.Entry<String, Shape> entry : hitShapes.entrySet()) {
            shapeHashSet.addAll(hitTestAndSelectShape(entry, touchPointList, radius));
        }
        resultList.addAll(shapeHashSet);
        for (Shape shape : shapeList) {
            if (resultList.contains(shape)) {
                shape.setSelected(true);
            } else {
                shape.setSelected(false);
            }
        }
        selectedShapeList.addAll(resultList);
        return resultList;
    }

    public List<Shape> getSelectedShapeList(){
        return selectedShapeList;
    }

    private List<Shape> hitTestAndSelectShape(Map.Entry<String, Shape> entry,
                                              TouchPointList touchPointList, float radius) {
        List<Shape> resultList = new ArrayList<>();
        for (TouchPoint touchPoint : touchPointList.getPoints()) {
            if (entry.getValue().hitTest(touchPoint.getX(), touchPoint.getY(), radius)) {
                resultList.add(entry.getValue());
            }
        }
        return resultList;
    }

    private void hitTestAndRemoveShape(Map.Entry<String, Shape> entry, final TouchPointList touchPointList, final float radius) {
        for(TouchPoint touchPoint : touchPointList.getPoints()) {
            if (entry.getValue().hitTest(touchPoint.getX(), touchPoint.getY(), radius)) {
                removeShape(entry.getValue(), true);
                return;
            }
        }
    }

    public void removeShapesByGroupId(final String removeGroupId) {
        if (StringUtils.isNullOrEmpty(removeGroupId)) {
            return;
        }

        List<Shape> removeShapes = new ArrayList<>();
        for (Shape shape : shapeList) {
            String groupId = shape.getGroupId();
            if (!StringUtils.isNullOrEmpty(groupId) && removeGroupId.equals(groupId)) {
                removeShapes.add(shape);
            }
        }

        for (Shape removeShape : removeShapes) {
            removeShape(removeShape, false);
        }
        removeShapes.clear();
    }

    public final List<Shape> getShapeList() {
        return shapeList;
    }

    public final List<Shape> deatchShapeList() {
        final List<Shape> list = shapeList;
        shapeList = null;
        return list;
    }

    public void render(final RenderContext renderContext, final RenderCallback callback) {
        if (shapeList == null) {
            return;
        }
        final Matrix renderMatrix = new Matrix();
        for (Shape shape : shapeList) {
            renderMatrix.reset();
            renderMatrix.postScale(shape.getScale(), shape.getScale());
            renderContext.setMatrix(renderMatrix);
            shape.render(renderContext);
            if (callback != null && callback.isRenderAbort()) {
                break;
            }
        }
    }

    private void updateSelectedRect() {
        //TODO:only consider 1 orientation shape circumstance.
        List<RectF> selectShapeRectList = new ArrayList<>();
        float orientation = 0f;
        for (Shape shape : shapeList) {
            if (shape.isSelected()) {
                RectF selectRect = new RectF();
                if ((shape instanceof EPDShape)) {
                    if (((BaseShape) shape).getOriginDisplayPath() != null) {
                        ((BaseShape) shape).getOriginDisplayPath().computeBounds(selectRect, true);
                    } else {
                        selectRect = shape.getBoundingRect();
                    }
                } else {
                    selectRect = shape.getBoundingRect();
                }
                orientation = shape.getSelectRectOrientation();
                selectShapeRectList.add(selectRect);
            }
        }
        if (selectShapeRectList.size() == 0) {
            clearSelectedRect();
        } else {
            currentSelectedRectF = unionSelectedShapeBoundingRect(orientation, selectShapeRectList);
        }
    }

    private void clearSelectedRect(){
        currentSelectedRectF.setOrientation(0);
        currentSelectedRectF.setRectF(new RectF());
    }

    public SelectedRectF getSelectedRect() {
        updateSelectedRect();
        return currentSelectedRectF;
    }

    private SelectedRectF unionSelectedShapeBoundingRect(float orientation, List<RectF> selectShapeBoundingRect) {
        RectF resultSelectRectF = new RectF();
        for (RectF targetRect : selectShapeBoundingRect) {
            if (resultSelectRectF.contains(targetRect) && Float.compare(orientation, 0) != 0) {
                orientation = 0;
            }
            resultSelectRectF.union(targetRect);
        }
        return new SelectedRectF(orientation, resultSelectRectF);
    }

    public PointF getRotateExtendPoint() {
        return MathUtils.calculateMiddlePointFromTwoPoint(rotateCacheRectTopLeft.x, rotateCacheRectTopLeft.y,
                rotateCacheRectTopRight.x, rotateCacheRectTopRight.y);
    }

    public void prepareShapePool(int shapeType) {
        currentShapeType = shapeType;
    }

    // create a new shape if not exist and make it as current shape.
    public final Shape getShapeFromPool() {
        return currentShape = ShapeFactory.createShape(currentShapeType);
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
    public static final NotePage loadPage(final Context context, final String docUniqueId,
                                          final String pageName, final String subPageName) {
        final List<ShapeModel> list = ShapeDataProvider.loadShapeList(context, docUniqueId, pageName, subPageName);
        final NotePage notePage = createPage(context, docUniqueId, pageName, subPageName);
        for(ShapeModel model : list) {
            final Shape shape = ShapeFactory.shapeFromModel(model);
            notePage.addShapeFromModel(shape);
        }
        return notePage;
    }

    public void loadPage(final Context context) {
        newAddedShapeList.clear();
        removedShapeList.clear();
        final List<ShapeModel> modelList = ShapeDataProvider.loadShapeList(context,
                getDocumentUniqueId(), getPageUniqueId(), getSubPageName());
        for(ShapeModel model : modelList) {
            addShapeFromModel(ShapeFactory.shapeFromModel(model));
        }
        setLoaded(true);
    }

    public static final NotePage createPage(final Context context, final String docUniqueId,
                                            final String pageName, final String subPageName) {
        return new NotePage(docUniqueId, pageName, subPageName);
    }

    public List<ShapeModel> getNewAddedShapeModeList() {
        List<ShapeModel> modelList = new ArrayList<>(newAddedShapeList.size());
        for(Shape shape : newAddedShapeList) {
            final ShapeModel model = ShapeFactory.modelFromShape(shape);
            modelList.add(model);
        }
        return modelList;
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
        List<ShapeModel> modelList = new ArrayList<>(newAddedShapeList.size());
        //TODO: new shape;
        for(Shape shape : newAddedShapeList) {
            final ShapeModel model = ShapeFactory.modelFromShape(shape);
            modelList.add(model);
        }
        if (modelList.size() > 0) {
            ShapeDataProvider.saveShapeList(context, modelList);
        }

        modelList.clear();

        //TODO: dirtyShape(shape transform);
        for (Shape shape : dirtyShapeList) {
            final ShapeModel model = ShapeFactory.modelFromShape(shape);
            modelList.add(model);
        }
        if (modelList.size() > 0) {
            ShapeDataProvider.updateShapeList(context, modelList);
        }

        //TODO: removed Shape;
        ShapeDataProvider.removeShapesByIdList(context, getRemovedShapeIdList());
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

    public void setScaleToSelectShapeList(float scale, boolean addToActionHistory) {
        if (selectedShapeList.size() <= 0) {
            return;
        }
        for (Shape shape : selectedShapeList) {
            shape.onScale(scale);
        }
        dirtyShapeList.clear();
        dirtyShapeList.addAll(selectedShapeList);
        if (addToActionHistory) {
            undoRedoManager.addToHistory(ShapeActions.
                    transformShapeListAction(preTransformShapeList, dirtyShapeList), false);
            preTransformShapeList.clear();
        }
    }

    public void setTranslateToSelectShapeList(float dX, float dY, boolean addToActionHistory) {
        if (selectedShapeList.size() <= 0) {
            return;
        }
        for (Shape shape : selectedShapeList) {
            shape.onTranslate(dX, dY);
        }
        dirtyShapeList.clear();
        dirtyShapeList.addAll(selectedShapeList);
        if (addToActionHistory) {
            undoRedoManager.addToHistory(ShapeActions.
                    transformShapeListAction(preTransformShapeList, dirtyShapeList), false);
            preTransformShapeList.clear();
        }
    }

    public void setRotationAngleToSelectShapeList(float angle, PointF originPoint, boolean addToActionHistory) {
        if (selectedShapeList.size() <= 0) {
            return;
        }
        for (Shape shape : selectedShapeList) {
            shape.onRotate(angle, originPoint);
        }
        Matrix m = new Matrix();
        m.setRotate(angle, originPoint.x, originPoint.y);
        float[] leftTopPointArray = new float[2];
        leftTopPointArray[0] = rotateCacheRectTopLeft.x;
        leftTopPointArray[1] = rotateCacheRectTopLeft.y;
        float[] rightTopPointArray = new float[2];
        rightTopPointArray[0] = rotateCacheRectTopRight.x;
        rightTopPointArray[1] = rotateCacheRectTopRight.y;
        m.mapPoints(leftTopPointArray);
        m.mapPoints(rightTopPointArray);
        rotateCacheRectTopLeft.x = leftTopPointArray[0];
        rotateCacheRectTopLeft.y = leftTopPointArray[1];

        rotateCacheRectTopRight.x = rightTopPointArray[0];
        rotateCacheRectTopRight.y = rightTopPointArray[1];

        dirtyShapeList.clear();
        dirtyShapeList.addAll(selectedShapeList);
        if (addToActionHistory) {
            undoRedoManager.addToHistory(ShapeActions.
                    transformShapeListAction(preTransformShapeList, dirtyShapeList), false);
            preTransformShapeList.clear();
        }
    }

    public void setMirrorEffectToSelectShapeList(MirrorType type, int translateDistance, boolean addToActionHistory) {
        if (selectedShapeList.size() <= 0) {
            return;
        }
        for (Shape shape : selectedShapeList) {
            shape.onMirror(type, translateDistance);
        }
        if (addToActionHistory) {
            undoRedoManager.addToHistory(ShapeActions.
                    transformShapeListAction(preTransformShapeList, dirtyShapeList), false);
            preTransformShapeList.clear();
        }
    }
}
