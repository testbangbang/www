package com.onyx.android.sdk.scribble.formshape;

/**
 * Created by ming on 2017/6/5.
 */

public class FormValue {

    private String text;

    private boolean isCheck;

    private int index;

    public FormValue() {
    }

    public FormValue(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public FormValue(String text) {
        this.text = text;
    }

    public FormValue(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public static FormValue create(boolean isCheck) {
        return new FormValue(isCheck);
    }

    public static FormValue create(String text) {
        return new FormValue(text);
    }

    public static FormValue create(int index) {
        return new FormValue(index);
    }
}
