package com.onyx.android.sdk.scribble.data;

import android.content.Context;

import com.onyx.android.sdk.data.PageInfo;
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
        close(context);
        setDocumentUniqueId(uniqueId);
        setParentUniqueId(parentLibraryUniqueId);
        setup(context);
        ensureDocumentNotBlank(context);
        markDocumentOpen(true);
    }

    public void create(final Context context,
                       final String uniqueId,
                       final String parentLibraryUniqueId) {
        close(context);
        setDocumentUniqueId(uniqueId);
        setParentUniqueId(parentLibraryUniqueId);
        ensureDocumentNotBlank(context);
        gotoFirst();
        markDocumentOpen(true);
    }

    public void close(final Context context) {
        pageDataMap.clear();
        documentUniqueId = null;
        parentUniqueId = null;
        currentPageIndex = 0;
        resetNoteDrawingArgs();
        markDocumentOpen(false);
    }

    public boolean isOpen() {
        return isOpen;
    }

    private void markDocumentOpen(boolean open) {
        isOpen = open;
    }

    public void save(final Context context, final String title) {
        NoteDataProvider.saveNote(context, getNoteModel(context, title));
        for(ListOrderedMap.Entry<String, NotePage> entry : pageDataMap.entrySet()) {
            entry.getValue().savePage(context);
        }
    }

    /**
     * sync data from noteDocument to noteModel.
     * @param context
     * @param title
     * @return
     */
    private NoteModel getNoteModel(final Context context, final String title) {
        NoteModel noteModel = NoteDataProvider.load(context, getDocumentUniqueId());
        if (noteModel == null) {
            noteModel = NoteModel.createNote(getDocumentUniqueId(), getParentUniqueId(), title);
        }
        final PageNameList pageNameList = new PageNameList();
        pageNameList.addAll(pageDataMap.keyList());
        noteModel.setPageNameList(pageNameList);
        noteModel.setCurrentShapeType(noteDrawingArgs.getCurrentShapeType());
        noteModel.strokeWidth = noteDrawingArgs.strokeWidth;
        noteModel.background = noteDrawingArgs.background;
        noteModel.strokeColor = noteDrawingArgs.strokeColor;
        noteModel.setLineLayoutBackground(noteDrawingArgs.getLineLayoutBackground());
        noteModel.setPosition(currentPageIndex);
        return noteModel;
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

    public int getBackground() {
        return noteDrawingArgs.background;
    }

    public void setBackground(int background) {
        noteDrawingArgs.background = background;
    }

    public void setBackgroundFilePath(String backgroundPath) {
        noteDrawingArgs.bgFilePath = backgroundPath;
    }

    public void setLineLayoutBackground(int background) {
        noteDrawingArgs.setLineLayoutBackground(background);
    }

    public int getLineLayoutBackground() {
        return noteDrawingArgs.getLineLayoutBackground();
    }

    public float getStrokeWidth() {
        return noteDrawingArgs.strokeWidth;
    }

    public void setStrokeWidth(float newStrokeWidth) {
        noteDrawingArgs.strokeWidth = newStrokeWidth;
    }

    public void setStrokeColor(int color) {
        noteDrawingArgs.strokeColor = color;
    }

    public int getStrokeColor() {
        return noteDrawingArgs.strokeColor;
    }

    public float getEraserRadius() {
        return noteDrawingArgs.eraserRadius;
    }

    public void setEraserRadius(final float r) {
        noteDrawingArgs.eraserRadius = r;
    }

    public NoteDrawingArgs.PenState getPenState() {
        return noteDrawingArgs.penState;
    }

    public void setPenState(final NoteDrawingArgs.PenState penState) {
        noteDrawingArgs.penState = penState;
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

    private void resetNoteDrawingArgs() {
        noteDrawingArgs.setCurrentShapeType(NoteDrawingArgs.defaultShape());
        noteDrawingArgs.strokeWidth = NoteModel.getDefaultStrokeWidth();
        noteDrawingArgs.background = NoteModel.getDefaultBackground();
        noteDrawingArgs.strokeColor = NoteModel.getDefaultStrokeColor();
        noteDrawingArgs.penState = NoteDrawingArgs.PenState.PEN_SCREEN_DRAWING;
        noteDrawingArgs.setLineLayoutBackground(NoteModel.getDefaultLineLayoutBackground());
    }

    // load args from model.
    private void setupDrawingArgs(final NoteModel noteModel) {
        resetNoteDrawingArgs();
        if (noteModel != null) {
            noteDrawingArgs.background = noteModel.getBackground();
            noteDrawingArgs.strokeColor = noteModel.getStrokeColor();
            noteDrawingArgs.setLineLayoutBackground(noteModel.getLineLayoutBackground());
            int currentShapeType = noteModel.getCurrentShapeType();
            if (currentShapeType != ShapeFactory.SHAPE_INVALID) {
                noteDrawingArgs.setCurrentShapeType(currentShapeType);
            }
            noteDrawingArgs.strokeWidth = noteModel.getStrokeWidth();
            currentPageIndex = noteModel.getPosition();
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

    public boolean clearFreeShapes(final Context context, final int index) {
        final NotePage notePage = getPageByIndex(index);
        if (notePage == null) {
            return false;
        }
        notePage.clearFreeShapes(true);
        return true;
    }

    public void removeShapesByTouchPointList(final Context context, final TouchPointList touchPointList, final float scale) {
        final NotePage notePage = getCurrentPage(context);
        notePage.removeShapesByTouchPointList(touchPointList, noteDrawingArgs.eraserRadius * scale);
    }

    public void removeShapesByGroupId(final Context context, final String groupId) {
        final NotePage notePage = getCurrentPage(context);
        notePage.removeShapesByGroupId(groupId);
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public String getCurrentPageUniqueId() {
        if (pageDataMap.size() > getCurrentPageIndex()) {
            return pageDataMap.get(getCurrentPageIndex());
        }
        return null;
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

    public boolean gotoPage(final int index) {
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
        if (notePage != null && notePage.isLoaded()) {
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
