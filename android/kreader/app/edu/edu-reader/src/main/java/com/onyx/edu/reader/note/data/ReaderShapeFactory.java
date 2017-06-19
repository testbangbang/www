package com.onyx.edu.reader.note.data;

import android.graphics.RectF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.formshape.FormValue;
import com.onyx.android.sdk.scribble.shape.*;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
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

    static public final int SHAPE_FORM_SINGLE_SELECTION = 0;
    static public final int SHAPE_FORM_MULTIPLE_SELECTION = 1;
    static public final int SHAPE_FORM_FILL = 2;
    static public final int SHAPE_FORM_QA = 3;

    public static boolean isUniqueFormShape(int formType) {
        return formType == SHAPE_FORM_SINGLE_SELECTION ||
                formType == SHAPE_FORM_MULTIPLE_SELECTION ||
                formType == SHAPE_FORM_FILL;
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

    public static final Shape shapeFromFormModel(final ReaderFormShapeModel shapeModel) {
        Shape shape = createShape(shapeModel.getShapeType());
        syncFormShapeDataFromModel(shape, shapeModel);
        return shape;
    }

    public static boolean isDFBShape(int shape) {
        return shape == SHAPE_PENCIL_SCRIBBLE || shape == SHAPE_BRUSH_SCRIBBLE || shape == SHAPE_OILY_PEN_SCRIBBLE || shape == SHAPE_FOUNTAIN_PEN_SCRIBBLE;
    }

    public static final ReaderNoteShapeModel modelFromShape(final Shape shape) {
        ReaderNoteShapeModel shapeModel;
        if (shape.isFormShape()) {
            shapeModel = new ReaderFormShapeModel();
            ((ReaderFormShapeModel) shapeModel).setFormId(shape.getFormId());
            ((ReaderFormShapeModel) shapeModel).setFormRect(shape.getFormRect());
            ((ReaderFormShapeModel) shapeModel).setFormType(shape.getFormType());
            ((ReaderFormShapeModel) shapeModel).setFormValue(shape.getFormValue());
            ((ReaderFormShapeModel) shapeModel).setLock(shape.isLock());
            ((ReaderFormShapeModel) shapeModel).setReview(shape.isReview());
        }else {
            shapeModel = new ReaderNoteShapeModel();
        }
        shapeModel.setDocumentUniqueId(shape.getDocumentUniqueId());
        shapeModel.setPageUniqueId(shape.getPageUniqueId());
        shapeModel.setShapeUniqueId(shape.getShapeUniqueId());
        shapeModel.setSubPageUniqueId(shape.getSubPageUniqueId());
        shapeModel.setBoundingRect(shape.getBoundingRect());
        shapeModel.setColor(shape.getColor());
        shapeModel.setPoints(shape.getPoints());
        shapeModel.setThickness(shape.getStrokeWidth());
        shapeModel.setShapeType(shape.getType());
        shapeModel.setPageOriginHeight(shape.getPageOriginHeight());
        shapeModel.setPageOriginWidth(shape.getPageOriginWidth());
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
        shape.setLock(model.isLock());
        shape.setReview(model.isReview());
    }

    public static Shape createFormShape(String documentUniqueId,
                                        PageInfo pageInfo,
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
        shape.setPageOriginHeight((int) pageInfo.getOriginHeight());
        shape.setPageOriginWidth((int) pageInfo.getOriginWidth());
        return shape;
    }

}
