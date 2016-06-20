package com.onyx.android.sdk.scribble.data;

import android.content.Context;
import com.onyx.android.sdk.scribble.shape.Shape;
import org.apache.commons.collections4.map.ListOrderedMap;

import java.util.LinkedHashSet;

/**
 * Created by zhuzeng on 6/20/16.
 */
public class ShapeDocument {

    private String documentUniqueId;
    private LinkedHashSet<String> pageIndex = new LinkedHashSet<String>();
    private ListOrderedMap<String, ShapePage> pageDataMap = new ListOrderedMap<String, ShapePage>();


    public void open(final Context context, final String uniqueId) {
        setDocumentUniqueId(uniqueId);
        setupPageIndex(context);
        setupPageDataMap(context);
    }

    private void setDocumentUniqueId(final String id) {
        documentUniqueId = id;
    }

    private void setupPageIndex(final Context context) {
        pageIndex.clear();
    }

    private void setupPageDataMap(final Context context) {

    }

    public void addShapeToPage(final int index, final String pageName, final Shape shape) {
        ShapePage shapePage = null;
        if (index < pageDataMap.size()) {
            shapePage = pageDataMap.getValue(index);
        }
        if (shapePage == null) {
            shapePage = new ShapePage();
            pageDataMap.put(index, pageName, shapePage);
        }
        if (shape != null) {
            shapePage.addShape(shape);
            shape.setUniqueId(documentUniqueId);
        }
    }


}
