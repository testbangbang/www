package com.onyx.phone.reader.reader.highlight;

import android.graphics.Color;
import android.graphics.RectF;

import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.utils.RectUtils;
import com.onyx.phone.reader.reader.data.ReaderDataHolder;
import com.onyx.phone.reader.reader.opengl.Highlight;
import com.onyx.phone.reader.reader.opengl.IOpenGLObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/5/3.
 */

public class SelectionManager {

    private ReaderDataHolder readerDataHolder;
    private ReaderSelection currentSelection;
    private List<RectF> selectionRectangles = new ArrayList<>();

    public SelectionManager(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    public void setCurrentSelection(ReaderSelection currentSelection) {
        this.currentSelection = currentSelection;
        updateSelectionRectangles();
    }

    public void clearSelection() {
        selectionRectangles.clear();
    }

    private void updateSelectionRectangles() {
        selectionRectangles.clear();
        List<RectF> rectangles = currentSelection.getRectangles();
        if (rectangles == null) {
            return;
        }
        selectionRectangles = RectUtils.mergeRectanglesByBaseLine(rectangles);
    }

    private float translateX(final float x) {
        int halfWidth = readerDataHolder.getDisplayWidth() /2;
        return (x - halfWidth) / halfWidth  ;
    }

    private float translateY(final float y) {
        int halfHeight = readerDataHolder.getDisplayHeight() / 2;
        return (halfHeight - y ) / halfHeight;
    }

    public List<IOpenGLObject> getSelectionRectangles() {
        List<IOpenGLObject> selections = new ArrayList<>();
        int arrayCount = 12;
        for (int i = 0; i < selectionRectangles.size(); i++) {
            RectF rectangle = selectionRectangles.get(i);
            float[] vertexArray = new float[arrayCount];

            vertexArray[0] = translateX(rectangle.left);
            vertexArray[1] = translateY(rectangle.top);
            vertexArray[2] = 0f;

            vertexArray[3] = translateX(rectangle.left);
            vertexArray[4] = translateY(rectangle.bottom);
            vertexArray[5] = 0f;

            vertexArray[6] = translateX(rectangle.right);
            vertexArray[7] = translateY(rectangle.top);
            vertexArray[8] = 0f;

            vertexArray[9] = translateX(rectangle.right);
            vertexArray[10] = translateY(rectangle.bottom);
            vertexArray[11] = 0f;

            Highlight highlight = Highlight.create(vertexArray, Color.GREEN, 0.5f);
            selections.add(highlight);
        }
        return selections;
    }

    public boolean hasSelection() {
        return selectionRectangles != null && selectionRectangles.size() > 0;
    }
}
