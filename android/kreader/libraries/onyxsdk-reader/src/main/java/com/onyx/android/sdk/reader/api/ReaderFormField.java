package com.onyx.android.sdk.reader.api;

import android.graphics.RectF;

import com.onyx.android.sdk.utils.Debug;

import java.util.List;

/**
 * Created by joy on 5/22/17.
 */

public class ReaderFormField {
    private String name;
    private RectF rect;

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
