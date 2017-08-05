package com.onyx.android.sdk.reader.api;

import android.graphics.RectF;

import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;

/**
 * Created by joy on 5/22/17.
 */

public class ReaderFormField {
    private String name;
    private RectF rect;

    public ReaderFormAttributes getFormAttributes() {
        return getFormAttributesByName(name);
    }

    public static ReaderFormAttributes getFormAttributesByName(String name) {
        if (StringUtils.isNullOrEmpty(name)) {
            return null;
        }
        return JSONObjectParseUtils.parseObject(name, ReaderFormAttributes.class);
    }

    public ReaderFormAction getFormAction() {
        ReaderFormAction action = ReaderFormAction.INVALID;
        ReaderFormAttributes formAttributes = getFormAttributes();
        if (formAttributes == null) {
            return action;
        }
        try {
            action = ReaderFormAction.valueOf(formAttributes.getAction().toUpperCase().trim());
        }catch (Exception e) {
            e.printStackTrace();
            return action;
        }
        return action;
    }

    public static ReaderFormType getFormTypeByName(String name) {
        ReaderFormType type = ReaderFormType.INVALID;
        ReaderFormAttributes formAttributes = getFormAttributesByName(name);
        if (formAttributes == null || formAttributes.getType() == null) {
            return type;
        }
        try {
            type = ReaderFormType.valueOf(formAttributes.getType().toUpperCase().trim());
        }catch (Exception e) {
            e.printStackTrace();
            return type;
        }
        return type;
    }

    protected ReaderFormField() {
        name = "";
        rect = new RectF();
    }

    protected ReaderFormField(String name) {
        this.name = name;
        rect = new RectF();
    }

    protected ReaderFormField(String name, float left, float top, float right, float bottom) {
        this.name = name;
        rect = new RectF(left, top, right, bottom);
    }

    public static void addToFieldList(List<ReaderFormField> list, ReaderFormField field) {
        list.add(field);
    }

    public String getName() {
        return name;
    }

    public RectF getRect() {
        return rect;
    }
}
