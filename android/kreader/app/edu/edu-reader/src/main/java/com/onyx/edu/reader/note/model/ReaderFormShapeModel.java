package com.onyx.edu.reader.note.model;

import android.graphics.RectF;

import com.onyx.android.sdk.scribble.data.ConverterRectangle;
import com.onyx.android.sdk.scribble.formshape.FormValue;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by ming on 2017/6/5.
 */

@Table(database = ReaderNoteDatabase.class)
public class ReaderFormShapeModel extends ReaderNoteShapeModel {

    @Column
    private String formId;

    @Column
    private Integer formType;

    @Column(typeConverter = ConverterRectangle.class)
    private RectF formRect;

    @Column(typeConverter = ConverterFormValue.class)
    private FormValue formValue;

    @Column
    private boolean lock;

    @Column
    private boolean review;

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Integer getFormType() {
        return formType;
    }

    public void setFormType(Integer formType) {
        this.formType = formType;
    }

    public RectF getFormRect() {
        return formRect;
    }

    public void setFormRect(RectF formRect) {
        this.formRect = formRect;
    }

    public FormValue getFormValue() {
        return formValue;
    }

    public void setFormValue(FormValue formValue) {
        this.formValue = formValue;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public boolean isReview() {
        return review;
    }

    public void setReview(boolean review) {
        this.review = review;
    }
}
