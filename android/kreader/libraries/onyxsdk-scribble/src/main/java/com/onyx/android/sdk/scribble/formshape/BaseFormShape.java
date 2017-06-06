package com.onyx.android.sdk.scribble.formshape;

import android.graphics.RectF;

import com.onyx.android.sdk.scribble.shape.BaseShape;

/**
 * Created by ming on 2017/6/5.
 */

public class BaseFormShape extends BaseShape {

    private String formId;
    private Integer formType;
    private RectF formRect;
    private FormValue formValue;

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
}

