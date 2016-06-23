package com.onyx.android.sdk.scribble.data;

import android.content.Context;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.utils.StringUtils;
import org.apache.commons.collections4.map.ListOrderedMap;

import java.util.LinkedHashSet;
import java.util.UUID;

/**
 * Created by zhuzeng on 6/20/16.
 * Maintain index and pageInfo in memory only. The database in sync in NotePage class.
 */
public class NoteDocument {

    private String documentUniqueId;
    private ListOrderedMap<String, NotePage> pageDataMap = new ListOrderedMap<String, NotePage>();
    private int currentPageIndex = 0;

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
        final LinkedHashSet<String> pageIndex = loadPageIndex(context);
        int index = 0;
        for(String key : pageIndex) {
            createPage(index++, key);
        }
    }

    private LinkedHashSet<String> loadPageIndex(final Context context) {
        final LinkedHashSet<String> index = new LinkedHashSet<String>();
        final NoteModel noteModel = NoteDataProvider.load(context, getDocumentUniqueId());
        if (noteModel.getPageNameList() == null) {
            return index;
        }
        for(String string : noteModel.getPageNameList().getPageNameList()) {
            index.add(string);
        }
        return index;
    }

    private void ensureDocumentNotBlank(final Context context) {
        if (pageDataMap.size() > 0) {
            return;
        }
        createBlankPage(context, 0);
    }

    private NotePage createPage(final int index, final String pageUniqueId) {
        NotePage notePage = new NotePage(getDocumentUniqueId(), pageUniqueId, null);
        pageDataMap.put(index, pageUniqueId, notePage);
        return notePage;
    }

    public NotePage getPage(final int index, final String pageUniqueId) {
        NotePage notePage = getPageByIndex(index);
        if (notePage != null) {
            return notePage;
        }
        return getPageByUniqueId(pageUniqueId);
    }

    public NotePage getPageByUniqueId(final String pageUniqueId) {
        return pageDataMap.get(pageUniqueId);
    }

    public NotePage getPageByIndex(final int index) {
        if (index >= 0 && index < pageDataMap.size()) {
            return pageDataMap.getValue(index);
        }
        return null;
    }

    public void addShapeToPage(final int index, final String pageUniqueId, final Shape shape) {
        final NotePage notePage = getPage(index, pageUniqueId);
        if (notePage != null && shape != null) {
            notePage.addShape(shape);
            shape.setDocumentUniqueId(getDocumentUniqueId());
            shape.setPageUniqueId(pageUniqueId);
        }
    }

    public boolean createBlankPage(final Context context, final int index) {
        final int value = Math.min(index, pageDataMap.size());
        createPage(value, ShapeUtils.generateUniqueId());
        return gotoPage(value);
    }

    public boolean removePage(final Context context, final int index) {
        final NotePage notePage = getPageByIndex(index);
        if (notePage == null) {
            return false;
        }
        notePage.remove();
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
