package com.onyx.android.sdk.reader.api;

import android.graphics.RectF;

import java.util.List;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderLink {


    public String getStartPosition();

    public String getEndPosition();

    public String getTargetPosition();

    public List<RectF> getDisplayRects();



}
