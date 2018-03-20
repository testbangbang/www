package com.onyx.jdread.main.model;

import android.databinding.BaseObservable;

/**
 * Created by suicheng on 2018/3/20.
 */
public class NoneResultModel extends BaseObservable {
    public int imageResId;
    public String text;

    public NoneResultModel(int imageResId, String text) {
        this.imageResId = imageResId;
        this.text = text;
    }
}
