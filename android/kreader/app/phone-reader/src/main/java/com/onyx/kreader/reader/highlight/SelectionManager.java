package com.onyx.kreader.reader.highlight;

import android.graphics.Color;
import android.graphics.RectF;

import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.utils.RectUtils;
import com.onyx.kreader.reader.data.ReaderDataHolder;
import com.onyx.kreader.reader.opengl.Highlight;
import com.onyx.kreader.reader.opengl.IOpenGLObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/5/3.
 */

public class SelectionManager {

    private ReaderDataHolder readerDataHolder;
    private ReaderSelection currentSelection;
    private List<IOpenGLObject> selectionRectangles = new ArrayList<>();

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
        rectangles = RectUtils.mergeRectanglesByBaseLine(rectangles);
        int arrayCount = 12;
        for (int i = 0; i < rectangles.size(); i++) {
            RectF rectangle = rectangles.get(i);
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

            Highlight square = Highlight.create(vertexArray, Color.GREEN, 0.5f);
            selectionRectangles.add(square);
        }
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
        return selectionRectangles;
    }

    public boolean hasSelection() {
        return selectionRectangles != null && selectionRectangles.size() > 0;
    }
}
