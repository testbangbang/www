package com.onyx.android.sdk.reader.api;

import android.graphics.RectF;

import java.util.List;

/**
 * Created by zhuzeng on 10/3/15.
 */
public abstract class ReaderSelection implements Cloneable{

    static public enum SelectionType {
        TEXT,
        INTERNAL_LINK,
        EXTERNAL_LINK,
        IMAGE,
        AUDIO,
        VIDEO,
    }

    @Override
    public abstract ReaderSelection clone();

    public abstract String getPageName();

    public abstract String getPagePosition();

    /**
     * Retrieve the start position inside document.
     * @return
     */
    public abstract String getStartPosition();

    /**
     * Retrieve end position.
     * @return
     */
    public abstract String getEndPosition();

    /**
     * Retrieve selected text.
     * @return
     */
    public abstract String getText();

    public abstract String getLeftText();

    public abstract String getRightText();

    /**
     * return true if selected text is a single word
     *
     * @return
     */
    public abstract boolean isSelectedOnWord();

    /**
     * Retrieve selected rectangle list in page coordinates system.
     * @return
     */
    public abstract List<RectF> getRectangles();


    /**
     * Get selection type.
     * @return
     */
    public abstract SelectionType getSelectionType();



}
