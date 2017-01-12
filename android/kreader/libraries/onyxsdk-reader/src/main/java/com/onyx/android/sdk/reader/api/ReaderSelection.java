package com.onyx.android.sdk.reader.api;

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

    public String getPageName();

    public String getPagePosition();

    /**
     * Retrieve the start position inside document.
     * @return
     */
    public String getStartPosition();

    /**
     * Retrieve end position.
     * @return
     */
    public String getEndPosition();

    /**
     * Retrieve selected text.
     * @return
     */
    public String getText();

    public String getLeftText();

    public String getRightText();

    /**
     * return true if selected text is a single word
     *
     * @return
     */
    public boolean isSelectedOnWord();

    /**
     * Retrieve selected rectangle list in page coordinates system.
     * @return
     */
    public List<RectF> getRectangles();


    /**
     * Get selection type.
     * @return
     */
    public SelectionType getSelectionType();



}
