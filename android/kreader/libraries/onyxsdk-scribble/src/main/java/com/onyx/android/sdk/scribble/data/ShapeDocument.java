package com.onyx.android.sdk.scribble.data;

import android.content.Context;
import com.onyx.android.sdk.scribble.shape.Shape;
import org.apache.commons.collections4.map.ListOrderedMap;

import java.util.LinkedHashSet;
import java.util.UUID;

/**
 * Created by zhuzeng on 6/20/16.
 * Maintain index and pageInfo in memory only. The database in sync in ShapePage class.
 */
public class ShapeDocument {

    private String documentUniqueId;
    private ListOrderedMap<String, ShapePage> pageDataMap = new ListOrderedMap<String, ShapePage>();
    private int currentPageIndex = 0;

    static public final String generatePageName() {
        return UUID.randomUUID().toString();
    }

    public void open(final Context context, final String uniqueId) {
        setDocumentUniqueId(uniqueId);
        setupPageDataMap(context);
        ensureDocumentNotBlank(context);
        gotoFirst();
    }

    private void setDocumentUniqueId(final String id) {
        documentUniqueId = id;
    }

    public String getDocumentUniqueId() {
        return documentUniqueId;
    }

    private void setupPageDataMap(final Context context) {
        final LinkedHashSet<String> pageIndex = new LinkedHashSet<String>();
        loadPageIndex(pageIndex);
        int index = 0;
        for(String key : pageIndex) {
            createPage(index++, key);
        }
    }

    private void loadPageIndex(final LinkedHashSet<String> index) {
    }

    private void ensureDocumentNotBlank(final Context context) {
        if (pageDataMap.size() > 0) {
            return;
        }
        createBlankPage(context, 0);
    }

    private ShapePage createPage(final int index, final String pageUniqueId) {
        ShapePage shapePage = new ShapePage(getDocumentUniqueId(), pageUniqueId, null);
        pageDataMap.put(index, pageUniqueId, shapePage);
        return shapePage;
    }

    public ShapePage getPage(final int index, final String pageUniqueId) {
        ShapePage shapePage = getPageByIndex(index);
        if (shapePage != null) {
            return shapePage;
        }
        return getPageByUniqueId(pageUniqueId);
    }

    public ShapePage getPageByUniqueId(final String pageUniqueId) {
        return pageDataMap.get(pageUniqueId);
    }

    public ShapePage getPageByIndex(final int index) {
        if (index >= 0 && index < pageDataMap.size()) {
            return pageDataMap.getValue(index);
        }
        return null;
    }

    public void addShapeToPage(final int index, final String pageUniqueId, final Shape shape) {
        final ShapePage shapePage = getPage(index, pageUniqueId);
        if (shapePage != null && shape != null) {
            shapePage.addShape(shape);
            shape.setDocumentUniqueId(getDocumentUniqueId());
            shape.setPageUniqueId(pageUniqueId);
        }
    }

    public boolean createBlankPage(final Context context, final int index) {
        final int value = Math.min(index, pageDataMap.size());
        createPage(value, generatePageName());
        return gotoPage(value);
    }

    public boolean removePage(final Context context, final int index) {
        final ShapePage shapePage = getPageByIndex(index);
        if (shapePage == null) {
            return false;
        }
        shapePage.remove();
        pageDataMap.remove(index);
        final int value = Math.min(index, pageDataMap.size() - 1);
        return gotoPage(value);
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public int getPageCount() {
        return pageDataMap.size();
    }

    public void gotoFirst() {
        gotoPage(0);
    }

    public void gotoLast() {
        gotoPage(Math.max(0, pageDataMap.size() - 1));
    }

    public boolean nextPage() {
        return gotoPage(currentPageIndex + 1);
    }

    public boolean prevPage() {
        return gotoPage(currentPageIndex - 1);
    }

    private boolean gotoPage(final int index) {
        if (index >= 0 && index < pageDataMap.size()) {
            currentPageIndex = index;
            return true;
        }
        return false;
    }


}
