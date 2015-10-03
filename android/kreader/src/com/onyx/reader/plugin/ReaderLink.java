package com.onyx.reader.plugin;

import android.graphics.RectF;

import java.util.List;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderLink {


    public ReaderDocumentPosition getStartPosition();

    public ReaderDocumentPosition getEndPosition();

    public ReaderDocumentPosition getTargetPosition();

    public List<RectF> getDisplayRects();



}
