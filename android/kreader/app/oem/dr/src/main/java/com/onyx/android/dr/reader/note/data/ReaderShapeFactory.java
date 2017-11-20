package com.onyx.android.dr.reader.note.data;

import com.onyx.android.dr.reader.note.model.ReaderNoteShapeModel;
import com.onyx.android.sdk.scribble.shape.BrushScribbleShape;
import com.onyx.android.sdk.scribble.shape.CircleShape;
import com.onyx.android.sdk.scribble.shape.LineShape;
import com.onyx.android.sdk.scribble.shape.NormalPencilShape;
import com.onyx.android.sdk.scribble.shape.RectangleShape;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.shape.TexShape;
import com.onyx.android.sdk.scribble.shape.TriangleShape;

/**
 * Created by zhuzeng on 9/16/16.
 */
public class ReaderShapeFactory {


    static public final int SHAPE_ERASER = -2;
    static public final int SHAPE_INVALID = -1;

    static public final int SHAPE_CIRCLE = 0;
    static public final int SHAPE_RECTANGLE = 1;
    static public final int SHAPE_PENCIL_SCRIBBLE = 2;
    static public final int SHAPE_OILY_PEN_SCRIBBLE = 3;
    static public final int SHAPE_FOUNTAIN_PEN_SCRIBBLE = 4;
    static public final int SHAPE_BRUSH_SCRIBBLE = 5;
    static public final int SHAPE_TEXT = 6;
    static public final int SHAPE_LINE = 7;
    static public final int SHAPE_TRIANGLE = 8;


    public static final Shape createShape(int type) {
        Shape shape;
        switch (type) {
            case ShapeFactory.SHAPE_PENCIL_SCRIBBLE:
                shape = new NormalPencilShape();
                break;
            case ShapeFactory.SHAPE_LINE:
                shape = new LineShape();
                break;
            case ShapeFactory.SHAPE_OILY_PEN_SCRIBBLE:
                shape = new NormalPencilShape();
                break;
            case ShapeFactory.SHAPE_FOUNTAIN_PEN_SCRIBBLE:
                shape = new NormalPencilShape();
                break;
            case ShapeFactory.SHAPE_BRUSH_SCRIBBLE:
                shape = new BrushScribbleShape();
                break;
            case ShapeFactory.SHAPE_CIRCLE:
                shape = new CircleShape();
                break;
            case ShapeFactory.SHAPE_RECTANGLE:
                shape = new RectangleShape();
                break;
            case ShapeFactory.SHAPE_TEXT:
                shape = new TexShape();
                break;
            case ShapeFactory.SHAPE_TRIANGLE:
                shape = new TriangleShape();
                break;
            default:
                shape = new NormalPencilShape();
                break;
        }
        return shape;
    }

    public static final Shape shapeFromModel(final ReaderNoteShapeModel shapeModel) {
        Shape shape = createShape(shapeModel.getShapeType());
        syncShapeDataFromModel(shape, shapeModel);
        return shape;
    }

    public static boolean isDFBShape(int shape) {
        return shape == SHAPE_PENCIL_SCRIBBLE || shape == SHAPE_BRUSH_SCRIBBLE || shape == SHAPE_OILY_PEN_SCRIBBLE || shape == SHAPE_FOUNTAIN_PEN_SCRIBBLE;
    }

    public static final ReaderNoteShapeModel modelFromShape(final Shape shape) {
        final ReaderNoteShapeModel shapeModel = new ReaderNoteShapeModel();
        shapeModel.setDocumentUniqueId(shape.getDocumentUniqueId());
        shapeModel.setPageUniqueId(shape.getPageUniqueId());
        shapeModel.setShapeUniqueId(shape.getShapeUniqueId());
        shapeModel.setSubPageUniqueId(shape.getSubPageUniqueId());
        shapeModel.setBoundingRect(shape.getBoundingRect());
        shapeModel.setColor(shape.getColor());
        shapeModel.setPoints(shape.getPoints());
        shapeModel.setThickness(shape.getStrokeWidth());
        shapeModel.setShapeType(shape.getType());
        return shapeModel;
    }

    private static void syncShapeDataFromModel(final Shape shape, final ReaderNoteShapeModel model) {
        shape.setDocumentUniqueId(model.getDocumentUniqueId());
        shape.setPageUniqueId(model.getPageUniqueId());
        shape.setColor(model.getColor());
        shape.setStrokeWidth(model.getThickness());
        shape.setShapeUniqueId(model.getShapeUniqueId());
        shape.addPoints(model.getPoints());
        shape.setPageOriginWidth(model.getPageOriginWidth());
    }


}