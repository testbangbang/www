package com.onyx.reader.api;

import android.graphics.RectF;

import java.util.List;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderSelection {

    static public enum SelectionType {
        TEXT,
        INTERNAL_LINK,
        EXTERNAL_LINK,
        IMAGE,
        AUDIO,
        VIDEO,
    }

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
     * Retrieve selected rectangle list in document coordinates system.
     * @return
     */
    public List<RectF> getRectangles();


    /**
     * Get selection type.
     * @return
     */
    public SelectionType getSelectionType();



}
