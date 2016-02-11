package com.onyx.kreader.api;

import android.graphics.RectF;

import java.util.List;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderLink {


    public ReaderPagePosition getStartPosition();

    public ReaderPagePosition getEndPosition();

    public ReaderPagePosition getTargetPosition();

    public List<RectF> getDisplayRects();



}
