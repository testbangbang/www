package com.onyx.edu.reader.note.data;

import android.content.Context;
import android.graphics.RectF;

import com.onyx.android.sdk.scribble.formshape.BaseFormShape;
import com.onyx.android.sdk.scribble.formshape.FormValue;
import com.onyx.android.sdk.scribble.shape.*;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;
import com.onyx.edu.reader.note.model.ReaderNoteShapeModel;

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

    static public final int FORM_SHAPE_SINGLE = 0;
    static public final int FORM_SHAPE_MULTIPLE = 1;
    static public final int FORM_SHAPE_FILL = 2;
    static public final int FORM_SHAPE_QA = 3;

    public static boolean isUniqueFormShape(int formType) {
        return formType == FORM_SHAPE_SINGLE ||
                formType == FORM_SHAPE_MULTIPLE ||
                formType == FORM_SHAPE_FILL;
    }

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
        shape.setFormShape(false);
    }

    private static void syncFormShapeDataFromModel(final Shape shape, final ReaderFormShapeModel model) {
        syncShapeDataFromModel(shape, model);
        shape.setFormShape(true);
        shape.setFormValue(model.getFormValue());
        shape.setFormId(model.getFormId());
        shape.setFormType(model.getFormType());
        shape.setFormRect(model.getFormRect());
    }

    public static ReaderFormShapeModel formModelFromShape(final Shape formShape) {
        ReaderFormShapeModel formShapeModel = new ReaderFormShapeModel();
        formShapeModel.setDocumentUniqueId(formShape.getDocumentUniqueId());
        formShapeModel.setFormId(formShape.getFormId());
        formShapeModel.setFormRect(formShape.getFormRect());
        formShapeModel.setFormType(formShape.getFormType());
        formShapeModel.setFormValue(formShape.getFormValue());
        return formShapeModel;
    }

    public static Shape createFormShape(String documentUniqueId,
                                        String formId,
                                        int formType,
                                        RectF formRect,
                                        FormValue value) {
        Shape shape = new BaseShape();
        shape.setDocumentUniqueId(documentUniqueId);
        shape.setFormShape(true);
        shape.setFormRect(formRect);
        shape.setFormId(formId);
        shape.setFormType(formType);
        shape.setFormValue(value);
        shape.setShapeUniqueId(ShapeUtils.generateUniqueId());
        return shape;
    }

}
