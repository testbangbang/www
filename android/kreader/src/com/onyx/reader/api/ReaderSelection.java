package com.onyx.reader.api;

import android.graphics.RectF;

import java.util.List;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderSelection {

    /**
     * Retrieve the start position.
     * @return
     */
    public ReaderDocumentPosition getStartPosition();

    /**
     * Retrieve end position.
     * @return
     */
    public ReaderDocumentPosition getEndPosition();

    /**
     * Retrieve selected text.
     * @return
     */
    public String getText();

    /**
     * Retrieve selected rectangle list.
     * @return
     */
    public List<RectF> getRectangles();



}
