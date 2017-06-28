package com.onyx.edu.reader.ui.events;

import android.content.Context;

/**
 * Created by ming on 2017/6/28.
 */

public class FormFieldSelectedEvent {

    private Context context;
    private String formId;
    private String value;

    public FormFieldSelectedEvent(Context context, String formId, String value) {
        this.context = context;
        this.formId = formId;
        this.value = value;
    }

    public static FormFieldSelectedEvent create(Context context, String formId, String value) {
        return new FormFieldSelectedEvent(context, formId, value);
    }

    public Context getContext() {
        return context;
    }

    public String getFormId() {
        return formId;
    }

    public String getValue() {
        return value;
    }
}
