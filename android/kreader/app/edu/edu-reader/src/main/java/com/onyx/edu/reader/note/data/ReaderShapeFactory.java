package com.onyx.edu.reader.note.data;

import android.graphics.RectF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.formshape.FormValue;
import com.onyx.android.sdk.scribble.shape.*;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.model.ReaderNoteShapeModel;
import com.onyx.edu.reader.note.model.SignatureShapeModel;

/**
 * Created by zhuzeng on 9/16/16.
 */
public class ReaderShapeFactory {

    static public final int SHAPE_FORM_SINGLE_SELECTION = 0;
    static public final int SHAPE_FORM_MULTIPLE_SELECTION = 1;
    static public final int SHAPE_FORM_FILL = 2;
    static public final int SHAPE_LIMIT_REGION_SCRIBBLE = 3;
    static public final int SHAPE_FREE_AREA_SCRIBBLE = 4;

    public static boolean isUniqueFormShape(int formType) {
        return formType == SHAPE_FORM_SINGLE_SELECTION ||
                formType == SHAPE_FORM_MULTIPLE_SELECTION ||
                formType == SHAPE_FORM_FILL;
    }

    public static boolean isScribbleFormShape(int formType) {
        return formType == SHAPE_LIMIT_REGION_SCRIBBLE ||
                formType == SHAPE_FREE_AREA_SCRIBBLE;
    }

    public static final Shape shapeFromModel(final ReaderNoteShapeModel shapeModel) {
        Shape shape = ShapeFactory.createShape(shapeModel.getShapeType());
        syncShapeDataFromModel(shape, shapeModel);
        return shape;
    }

    public static final Shape shapeFromFormModel(final ReaderFormShapeModel shapeModel) {
        Shape shape = ShapeFactory.createShape(shapeModel.getShapeType());
        syncFormShapeDataFromModel(shape, shapeModel);
        return shape;
    }

    public static final Shape shapeFromSignatureModel(final SignatureShapeModel shapeModel) {
        Shape shape = ShapeFactory.createShape(shapeModel.getShapeType());
        syncSignatureShapeFromModel(shape, shapeModel);
        return shape;
    }

    public static boolean isDFBShape(int shape) {
        return shape == ShapeFactory.SHAPE_PENCIL_SCRIBBLE ||
                shape == ShapeFactory.SHAPE_BRUSH_SCRIBBLE ||
                shape == ShapeFactory.SHAPE_OILY_PEN_SCRIBBLE ||
                shape == ShapeFactory.SHAPE_FOUNTAIN_PEN_SCRIBBLE;
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
        } else {
            shapeModel = new ReaderNoteShapeModel();
        }
        syncModelFromShape(shapeModel, shape);
        return shapeModel;
    }

    public static SignatureShapeModel signatureModelFromShape(final Shape shape, final String accountId) {
        SignatureShapeModel shapeModel = new SignatureShapeModel();
        shapeModel.setAccountId(accountId);
        shapeModel.setSignatureRect(shape.getFormRect());
        syncModelFromShape(shapeModel, shape);
        return shapeModel;
    }

    private static void syncModelFromShape(final ReaderNoteShapeModel shapeModel, final Shape shape) {
        shapeModel.setDocumentUniqueId(shape.getDocumentUniqueId());
        shapeModel.setPageUniqueId(shape.getPageUniqueId());
        shapeModel.setShapeUniqueId(shape.getShapeUniqueId());
        shapeModel.setSubPageUniqueId(shape.getSubPageUniqueId());
        shapeModel.setBoundingRect(shape.getBoundingRect());
        shapeModel.setColor(shape.getColor());
        shapeModel.setPoints(shape.getPoints());
        shapeModel.setThickness(shape.getStrokeWidth());
        shapeModel.setShapeType(shape.getType());
        shapeModel.setExtraAttributes(shape.getShapeExtraAttributes());
        shapeModel.setPageOriginHeight(shape.getPageOriginHeight());
        shapeModel.setPageOriginWidth(shape.getPageOriginWidth());
    }

    private static void syncShapeDataFromModel(final Shape shape, final ReaderNoteShapeModel model) {
        shape.setDocumentUniqueId(model.getDocumentUniqueId());
        shape.setPageUniqueId(model.getPageUniqueId());
        shape.setColor(model.getColor());
        shape.setStrokeWidth(model.getThickness());
        shape.setShapeUniqueId(model.getShapeUniqueId());
        shape.addPoints(model.getPoints());
        shape.setShapeExtraAttributes(model.getExtraAttributes());
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

    private static void syncSignatureShapeFromModel(final Shape shape, final SignatureShapeModel model) {
        syncShapeDataFromModel(shape, model);
        shape.setFormRect(model.getSignatureRect());
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
