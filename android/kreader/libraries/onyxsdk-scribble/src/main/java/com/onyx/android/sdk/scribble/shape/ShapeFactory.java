package com.onyx.android.sdk.scribble.shape;

import com.onyx.android.sdk.scribble.data.ShapeModel;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuzeng on 4/20/16.
 */
public class ShapeFactory {

    static public final int SHAPE_SELECTOR = -3;
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
    static public final int SHAPE_ANNOTATION = 9;
    static public final int SHAPE_TRIANGLE_45 = 10;
    static public final int SHAPE_TRIANGLE_60 = 11;
    static public final int SHAPE_TRIANGLE_90 = 12;
    static public final int SHAPE_FORM_TEXT = 13;

    static public final int POSITION_FREE = 0;
    static public final int POSITION_LINE_LAYOUT = 1;

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
            case ShapeFactory.SHAPE_TRIANGLE_45:
                shape = new Triangle45Shape();
                break;
            case ShapeFactory.SHAPE_TRIANGLE_60:
                shape = new Triangle60Shape();
                break;
            case ShapeFactory.SHAPE_TRIANGLE_90:
                shape = new Triangle90Shape();
                break;
            case ShapeFactory.SHAPE_ANNOTATION:
                shape = new AnnotationShape();
                break;
            case ShapeFactory.SHAPE_FORM_TEXT:
                shape = new FormTextShape();
                break;
            default:
                shape = new NormalPencilShape();
                break;
        }
        return shape;
    }

    public static final Shape shapeFromModel(final ShapeModel shapeModel) {
        Shape shape = createShape(shapeModel.getShapeType());
        syncShapeDataFromModel(shape, shapeModel);
        return shape;
    }

    public static boolean isDFBShape(int shape) {
        return shape == SHAPE_PENCIL_SCRIBBLE || shape == SHAPE_BRUSH_SCRIBBLE || shape == SHAPE_OILY_PEN_SCRIBBLE || shape == SHAPE_FOUNTAIN_PEN_SCRIBBLE;
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
        shapeModel.setGroupId(shape.getGroupId());
        shapeModel.setLayoutType(shape.getLayoutType());
        shapeModel.setExtraAttributesBean(shape.getShapeExtraAttributes());
        shapeModel.setOrientation(shape.getOrientation());
        return shapeModel;
    }

    private static void syncShapeDataFromModel(final Shape shape, final ShapeModel model) {
        shape.setDocumentUniqueId(model.getDocumentUniqueId());
        shape.setPageUniqueId(model.getPageUniqueId());
        shape.setColor(model.getColor());
        shape.setStrokeWidth(model.getThickness());
        shape.setShapeUniqueId(model.getShapeUniqueId());
        shape.addPoints(model.getPoints());
        shape.setPageOriginWidth(model.getPageOriginWidth());
        shape.setGroupId(model.getGroupId());
        shape.setLayoutType(model.getLayoutType());
        shape.setShapeExtraAttributes(model.getExtraAttributesBean());
        shape.setOrientation(model.getOrientation());
    }

    public static Map<String, List<Shape>> getSubPageSpanShapeList(List<Shape> subPageShapes) {
        Map<String, List<Shape>> subPageSpanShapeMap = new LinkedHashMap<>();
        for (Shape subPageShape : subPageShapes) {
            if (subPageShape.isFreePosition() || StringUtils.isNullOrEmpty(subPageShape.getGroupId())) {
                continue;
            }
            String groupId = subPageShape.getGroupId();
            if (subPageSpanShapeMap.containsKey(groupId)) {
                subPageSpanShapeMap.get(groupId).add(subPageShape);
            }else {
                List<Shape> spanShapes = new ArrayList<>();
                spanShapes.add(subPageShape);
                subPageSpanShapeMap.put(groupId,spanShapes);
            }
        }
        return subPageSpanShapeMap;
    }
}
