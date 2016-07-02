package com.onyx.android.sdk.scribble.data;

import android.content.Context;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.utils.StringUtils;
import org.apache.commons.collections4.map.ListOrderedMap;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by zhuzeng on 6/20/16.
 * Maintain index and pageInfo in memory only. The database in sync in NotePage class.
 */
public class NoteDocument {

    private String documentUniqueId;
    private String parentUniqueId;
    private ListOrderedMap<String, NotePage> pageDataMap = new ListOrderedMap<String, NotePage>();
    private int currentPageIndex = 0;
    private boolean isOpen = false;
    private NoteDrawingArgs noteDrawingArgs = new NoteDrawingArgs();

    public void open(final Context context,
                     final String uniqueId,
                     final String parentLibraryUniqueId) {
        setDocumentUniqueId(uniqueId);
        setParentUniqueId(parentLibraryUniqueId);
        setup(context);
        ensureDocumentNotBlank(context);
        gotoFirst();
        markDocumentOpen();
    }

    public void cleanDocument(final Context context) {
        pageDataMap.clear();
    }

    public boolean isOpen() {
        return isOpen;
    }

    private void markDocumentOpen() {
        isOpen = true;
    }

    public void save(final Context context, final String title) {
        NoteDataProvider.saveNote(context, getNoteModel(context, title));
        for(ListOrderedMap.Entry<String, NotePage> entry : pageDataMap.entrySet()) {
            entry.getValue().savePage(context);
        }
    }

    private NoteModel getNoteModel(final Context context, final String title) {
        NoteModel noteModel = NoteDataProvider.load(context, getDocumentUniqueId());
        if (noteModel != null) {
            return noteModel;
        }
        NoteModel model = NoteModel.createNote(getDocumentUniqueId(), getParentUniqueId(), title);
        final PageNameList pageNameList = new PageNameList();
        pageNameList.addAll(pageDataMap.keyList());
        model.setPageNameList(pageNameList);
        model.strokeWidth = noteDrawingArgs.strokeWidth;
        return model;
    }

    private void setDocumentUniqueId(final String id) {
        documentUniqueId = id;
    }

    public String getDocumentUniqueId() {
        return documentUniqueId;
    }

    public String getParentUniqueId() {
        return parentUniqueId;
    }

    public void setParentUniqueId(String parentUniqueId) {
        this.parentUniqueId = parentUniqueId;
    }

    public PageNameList getPageNameList() {
        final PageNameList pageNameList = new PageNameList();
        pageNameList.addAll(pageDataMap.keyList());
        return pageNameList;
    }

    private void setup(final Context context) {
        final NoteModel noteModel = NoteDataProvider.load(context, getDocumentUniqueId());
        setupPageDataMap(noteModel);
        setupDrawingArgs(noteModel);
    }

    private void setupPageDataMap(final NoteModel noteModel) {
        final LinkedHashSet<String> pageIndex = loadPageIndex(noteModel);
        int index = 0;
        for(String key : pageIndex) {
            createPage(index++, key);
        }
    }

    private void setupDrawingArgs(final NoteModel noteModel) {
        noteDrawingArgs.strokeWidth = NoteModel.getDefaultStrokeWidth();
        if (noteModel != null) {
            noteDrawingArgs.strokeWidth = noteModel.getStrokeWidth();
        }
    }

    public NoteDrawingArgs getNoteDrawingArgs() {
        return noteDrawingArgs;
    }

    private LinkedHashSet<String> loadPageIndex(final NoteModel noteModel) {
        final LinkedHashSet<String> index = new LinkedHashSet<String>();
        if (noteModel == null || noteModel.getPageNameList() == null) {
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
        ensureDocumentNotBlank(context);
        final int value = Math.min(index, pageDataMap.size() - 1);
        return gotoPage(value);
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public String getCurrentPageUniqueId() {
        return pageDataMap.get(getCurrentPageIndex());
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

    public boolean loadShapePages(final Context context, final List<PageInfo> visiblePages) {
        for(PageInfo pageInfo: visiblePages) {
            getNotePage(context, pageInfo.getName());
        }
        return true;
    }

    public NotePage getNotePage(final Context context, final String pageUniqueName) {
        if (StringUtils.isNullOrEmpty(pageUniqueName)) {
            return null;
        }
        NotePage notePage = null;
        if (pageDataMap.containsKey(pageUniqueName)) {
            notePage = pageDataMap.get(pageUniqueName);
        } else {
            notePage = NotePage.createPage(context, getDocumentUniqueId(), pageUniqueName, null);
            pageDataMap.put(pageUniqueName, notePage);
        }
        if (notePage != null && notePage.hasShapes()) {
            return notePage;
        }
        notePage.loadPage(context);
        return notePage;
    }

    public NotePage getCurrentPage(final Context context) {
        final String pageId = getCurrentPageUniqueId();
        return getNotePage(context, pageId);
    }


}
