package com.onyx.android.sdk.scribble.shape;

import com.onyx.android.sdk.scribble.data.ShapeModel;

/**
 * Created by zhuzeng on 4/20/16.
 */
public class ShapeFactory {

    static public final int SHAPE_ERASER = -2;
    static public final int SHAPE_INVALID = -1;

    static public final int SHAPE_CIRCLE = 0;
    static public final int SHAPE_RECTANGLE = 1;
    static public final int SHAPE_NORMAL_SCRIBBLE = 2;
    static public final int SHAPE_VARY_SCRIBBLE = 3;
    static public final int SHAPE_TEXT = 4;
    static public final int SHAPE_LINE = 5;

    public static final Shape createShape(int type) {
        Shape shape;
        switch (type) {
            case ShapeFactory.SHAPE_NORMAL_SCRIBBLE:
                shape = new NormalScribbleShape();
                break;
            case ShapeFactory.SHAPE_LINE:
                shape = new LineShape();
                break;
            default:
                shape = new NormalScribbleShape();
        }
        return shape;
    }

    public static final Shape shapeFromModel(final ShapeModel shapeModel) {
        switch (shapeModel.getShapeType()) {
            case SHAPE_NORMAL_SCRIBBLE:
                return createNormalScribbleShape(shapeModel);
            case SHAPE_VARY_SCRIBBLE:
                return createVaryScribbleShape(shapeModel);
            case SHAPE_CIRCLE:
                return createCircleShape(shapeModel);
            case SHAPE_RECTANGLE:
                return createRectangleShape(shapeModel);
            case SHAPE_LINE:
                return createLineShape(shapeModel);
            default:
                return createNormalScribbleShape(shapeModel);
        }
    }

    public static boolean isDFBShape(int shape) {
        return shape == SHAPE_NORMAL_SCRIBBLE || shape == SHAPE_VARY_SCRIBBLE;
    }

    public static final ShapeModel modelFromShape(final Shape shape) {
        final ShapeModel shapeModel = new ShapeModel();
        shapeModel.setDocumentUniqueId(shape.getDocumentUniqueId());
        shapeModel.setPageUniqueId(shape.getPageUniqueId());
        shapeModel.setShapeUniqueId(shape.getShapeUniqueId());
        shapeModel.setBoundingRect(shape.getBoundingRect());
        shapeModel.setColor(shape.getColor());
        shapeModel.setPoints(shape.getPoints());
        shapeModel.setThickness(shape.getStrokeWidth());
        shapeModel.setShapeType(shape.getType());
        return shapeModel;
    }

    private static final Shape createNormalScribbleShape(final ShapeModel shapeModel) {
        final NormalScribbleShape shape = new NormalScribbleShape();
        syncShapeDataFromModel(shape, shapeModel);
        return shape;
    }

    private static final Shape createVaryScribbleShape(final ShapeModel shapeModel) {
        final BrushScribbleShape shape = new BrushScribbleShape();
        syncShapeDataFromModel(shape, shapeModel);
        return shape;
    }

    private static final Shape createCircleShape(final ShapeModel shapeModel) {
        final CircleShape shape = new CircleShape();
        syncShapeDataFromModel(shape, shapeModel);
        return shape;
    }

    private static final Shape createRectangleShape(final ShapeModel shapeModel) {
        final RectangleShape shape = new RectangleShape();
        syncShapeDataFromModel(shape, shapeModel);
        return shape;
    }

    private static final Shape createLineShape(final ShapeModel shapeModel) {
        final LineShape shape = new LineShape();
        syncShapeDataFromModel(shape, shapeModel);
        return shape;
    }

    private static void syncShapeDataFromModel(final Shape shape, final ShapeModel model) {
        shape.setDocumentUniqueId(model.getDocumentUniqueId());
        shape.setPageUniqueId(model.getPageUniqueId());
        shape.setColor(model.getColor());
        shape.setStrokeWidth(model.getThickness());
        shape.setShapeUniqueId(model.getShapeUniqueId());
        shape.addPoints(model.getPoints());
        shape.setPageOriginWidth(model.getPageOriginWidth());
    }
}
