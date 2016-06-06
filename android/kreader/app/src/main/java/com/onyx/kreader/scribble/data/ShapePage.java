package com.onyx.kreader.scribble.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import com.onyx.kreader.scribble.shape.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 4/23/16.
 * Manager for a list of shapes in single page.
 * To make it easy for activity or other class to manage shapes.
 * ShapeManager->ShapePage->Shape->ShapeModel
 */
public class ShapePage {

    private boolean dirty = false;
    private String docUniqueId;
    private String pageName;
    private String subPageName;

    private List<Shape> shapeList = new ArrayList<Shape>();
    private List<Shape> newShapeList = new ArrayList<Shape>();
    private List<Shape> removeShapeList = new ArrayList<Shape>();

    private int currentShapeType;
    private Shape currentShape;
    private Matrix pageMatrix;

    public ShapePage() {

    }

    public ShapePage(final String docId, final String pn, final String spn) {
        docUniqueId = docId;
        pageName = pn;
        subPageName = spn;
    }

    public final String getDocUniqueId() {
        return docUniqueId;
    }

    public final String getPageName() {
        return pageName;
    }

    public final String getSubPageName() {
        return subPageName;
    }

    public void addShape(final Shape shape) {
        shapeList.add(shape);
    }

    public final List<Shape> getShapeList() {
        return shapeList;
    }

    public final List<Shape> deatchShapeList() {
        final List<Shape> list = shapeList;
        shapeList = null;
        return list;
    }

    public void setPageMatrix(final Matrix matrix) {
        pageMatrix = matrix;
    }

    public final TouchPoint normalizedTouchPoint(final float x, final float y, final float pressure, final float size, final long timestamp) {
        return new TouchPoint();
    }

    public void render(final Bitmap bitmap, final Paint paint) {
        if (shapeList == null) {
            return;
        }
        Canvas canvas = new Canvas(bitmap);
        for(Shape shape : shapeList) {
            shape.render(pageMatrix, canvas, paint);
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
    public static final ShapePage loadPage(final Context context, final String docUniqueId, final String pageName, final String subPageName) {
        final List<ShapeModel> list = ShapeDataProvider.loadShapeList(context, docUniqueId, pageName, subPageName);
        final ShapePage shapePage = createPage(context, docUniqueId, pageName, subPageName);
        for(ShapeModel model : list) {
            final Shape shape = ShapeFactory.shapeFromModel(model);
            shapePage.addShape(shape);
        }
        return shapePage;
    }

    public static final ShapePage createPage(final Context context, final String docUniqueId, final String pageName, final String subPageName) {
        final ShapePage page = new ShapePage(docUniqueId, pageName, subPageName);
        return page;
    }

    /**
     * save new added shapes and remove shapes has been removed.
     * @return
     */
    public boolean savePage() {
        for(Shape shape : newShapeList) {
            final ShapeModel model = ShapeFactory.modelFromShape(shape);
            model.save();
        }

        for(Shape shape: removeShapeList) {
            ShapeDataProvider.removeShape(null, shape.getUniqueId());
        }
        return true;
    }
}
