package com.onyx.reader.plugin;

import android.graphics.PointF;
import android.graphics.RectF;

import java.util.List;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderTextSelection {

    public ReaderDocumentPosition getStartPosition();

    public ReaderDocumentPosition getEndPosition();

    public String getText();

    public List<RectF> getRectangles();



}
