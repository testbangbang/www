package com.onyx.kreader.scribble.shape;

import com.onyx.kreader.scribble.data.ShapeModel;

/**
 * Created by zhuzeng on 4/20/16.
 */
public class ShapeFactory {

    static public final int SHAPE_INVALID = -1;
    static public final int SHAPE_CIRCLE = 0;
    static public final int SHAPE_RECTANGLE = 1;
    static public final int SHAPE_NORMAL_SCRIBBLE = 2;
    static public final int SHAPE_VARY_SCRIBBLE = 3;
    static public final int SHAPE_TEXT = 4;

    // create from pool if needed.
    public static final Shape createShape(int type) {
        return null;
    }

    public static final Shape shapeFromModel(final ShapeModel shapeModel) {
        switch (shapeModel.getShapeType()) {
            case SHAPE_NORMAL_SCRIBBLE:
                return createNormalScribbleShape(shapeModel);
            case SHAPE_VARY_SCRIBBLE:
                return createVaryScribbleShape(shapeModel);
            default:
                return createNormalScribbleShape(shapeModel);
        }
    }

    private static final Shape createNormalScribbleShape(final ShapeModel shapeModel) {
        final NormalScribbleShape shape = new NormalScribbleShape();
        shape.addPoints(shapeModel.getPoints());
        return shape;
    }

    private static final Shape createVaryScribbleShape(final ShapeModel shapeModel) {
        final BrushScribbleShape shape = new BrushScribbleShape();
        shape.addPoints(shapeModel.getPoints());
        return shape;
    }
}
