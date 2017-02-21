package com.onyx.android.sdk.reader.plugins.djvu;

import android.graphics.RectF;

import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.host.impl.ReaderTextSplitterImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 3/3/16.
 */
public class DjvuSelection extends ReaderSelection {

    @SuppressWarnings("unused")
    public static void addToSelectionList(List<ReaderSelection> list, String text, int [] rectangles) {
        list.add(new DjvuSelection(text, rectangles));
    }

    private String text;
    private List<RectF> rectangles = new ArrayList<RectF>();

    public DjvuSelection(String text, int[] data) {
        this.text = text;
        for(int i = 0; i < data.length / 4; ++i) {
            rectangles.add(new RectF(data[i * 4], data[i * 4 + 1], data[i * 4 + 2], data[i * 4 + 3]));
        }
    }

    public DjvuSelection(List<RectF> rectangles, String text) {
        this.rectangles = rectangles;
        this.text = text;
    }

    @Override
    public DjvuSelection clone() {
        List<RectF> copy = new ArrayList<>();
        for (RectF rectangle : rectangles) {
            copy.add(new RectF(rectangle));
        }
        return new DjvuSelection(copy, text);
    }

    @Override
    public String getPageName() {
        return null;
    }

    @Override
    public String getPagePosition() {
        return null;
    }

    /**
     * Retrieve the start position inside document.
     *
     * @return
     */
    @Override
    public String getStartPosition() {
        return null;
    }

    /**
     * Retrieve end position.
     *
     * @return
     */
    @Override
    public String getEndPosition() {
        return null;
    }

    /**
     * Retrieve selected text.
     *
     * @return
     */
    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getLeftText() {
        return "";
    }

    @Override
    public String getRightText() {
        return "";
    }

    @Override
    public boolean isSelectedOnWord() {
        return ReaderTextSplitterImpl.sharedInstance().isWord(getText());
    }

    /**
     * Retrieve selected rectangle list in page coordinates system.
     *
     * @return
     */
    @Override
    public List<RectF> getRectangles() {
        return rectangles;
    }

    /**
     * Get selection type.
     *
     * @return
     */
    @Override
    public SelectionType getSelectionType() {
        return SelectionType.TEXT;
    }
}
