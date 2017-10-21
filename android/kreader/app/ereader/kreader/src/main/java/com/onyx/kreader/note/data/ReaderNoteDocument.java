package com.onyx.kreader.note.data;

import android.content.Context;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.PageRange;
import com.onyx.android.sdk.scribble.data.*;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.note.model.ReaderNoteDataProvider;
import com.onyx.kreader.note.model.ReaderNoteDocumentModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by zhuzeng on 9/16/16.
 */
public class ReaderNoteDocument {

    private String documentUniqueId;
    private String parentUniqueId;
    private ReaderNotePageNameMap pageIndex = new ReaderNotePageNameMap();
    private LinkedHashMap<String, ReaderNotePage> pageMap = new LinkedHashMap<>();
    private NoteDrawingArgs noteDrawingArgs = new NoteDrawingArgs();
    private boolean isOpen = false;

    public void open(final Context context,
                     final String uniqueId,
                     final String parentLibraryUniqueId) {
        close(context);
        setDocumentUniqueId(uniqueId);
        setParentUniqueId(parentLibraryUniqueId);
        setup(context);
        markDocumentOpen(true);
    }

    public void create(final Context context,
                       final String uniqueId,
                       final String parentLibraryUniqueId) {
        close(context);
        setDocumentUniqueId(uniqueId);
        setParentUniqueId(parentLibraryUniqueId);
        markDocumentOpen(true);
    }

    public void close(final Context context) {
        pageIndex.clear();
        pageMap.clear();
        documentUniqueId = null;
        parentUniqueId = null;
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
        ReaderNoteDataProvider.saveDocument(context, getReaderNoteDocumentModel(context, title));
        for(LinkedHashMap.Entry<String, ReaderNotePage> entry : getPageMap().entrySet()) {
            entry.getValue().savePage(context);
        }
    }

    private ReaderNoteDocumentModel getReaderNoteDocumentModel(final Context context, final String title) {
        ReaderNoteDocumentModel noteModel = ReaderNoteDataProvider.loadDocument(context, getDocumentUniqueId());
        if (noteModel == null) {
            noteModel = ReaderNoteDocumentModel.createNote(getDocumentUniqueId(), getParentUniqueId(), title);
        }
        noteModel.setReaderNotePageNameMap(pageIndex);
        noteModel.setCurrentShapeType(noteDrawingArgs.getCurrentShapeType());
        noteModel.setStrokeWidth(noteDrawingArgs.strokeWidth);
        noteModel.setBackground(noteDrawingArgs.background);
        noteModel.setStrokeColor(noteDrawingArgs.strokeColor);
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

    public int getCurrentShapeType() {
        return noteDrawingArgs.getCurrentShapeType();
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

    private void setup(final Context context) {
        final ReaderNoteDocumentModel documentModel = ReaderNoteDataProvider.loadDocument(context, getDocumentUniqueId());
        setupPageIndex(documentModel);
        setupDrawingArgs(documentModel);
    }

    private void setupPageIndex(final ReaderNoteDocumentModel noteModel) {
        pageIndex = loadPageIndex(noteModel);
    }

    private void resetNoteDrawingArgs() {
        noteDrawingArgs.setCurrentShapeType(NoteDrawingArgs.defaultShape());
        noteDrawingArgs.strokeWidth = NoteModel.getDefaultStrokeWidth();
        noteDrawingArgs.background = NoteModel.getDefaultBackground();
        noteDrawingArgs.strokeColor = NoteModel.getDefaultStrokeColor();
        noteDrawingArgs.penState = NoteDrawingArgs.PenState.PEN_SCREEN_DRAWING;
    }

    public void reloadPageIndex(Context context) {
        final ReaderNoteDocumentModel documentModel = ReaderNoteDataProvider.loadDocument(context, getDocumentUniqueId());
        setupPageIndex(documentModel);
    }

    // loadDocument args from model.
    private void setupDrawingArgs(final ReaderNoteDocumentModel noteModel) {
        resetNoteDrawingArgs();
        if (noteModel != null) {
            noteDrawingArgs.background = noteModel.getBackground();
            noteDrawingArgs.strokeColor = noteModel.getStrokeColor();
            noteDrawingArgs.setCurrentShapeType(noteModel.getCurrentShapeType());
            noteDrawingArgs.strokeWidth = noteModel.getStrokeWidth();
        }
    }

    public NoteDrawingArgs getNoteDrawingArgs() {
        return noteDrawingArgs;
    }

    private ReaderNotePageNameMap loadPageIndex(final ReaderNoteDocumentModel noteModel) {
        final ReaderNotePageNameMap index = new ReaderNotePageNameMap();
        if (noteModel == null || noteModel.getReaderNotePageNameMap() == null) {
            return index;
        }
        return noteModel.getReaderNotePageNameMap();
    }

    private ReaderNotePageNameMap getPageIndex() {
        return pageIndex;
    }

    private HashMap<String, ReaderNotePage> getPageMap() {
        return pageMap;
    }

    public int getSubPageCount(final PageRange range) {
        return getPageIndex().getSubPageCount(range);
    }

    public ReaderNotePage createPage(final PageRange range, int subPageIndex) {
        String subPageId = ShapeUtils.generateUniqueId();
        createIndexEntry(range, subPageIndex, subPageId);
        ReaderNotePage readerNotePage = ensureDataEntry(range.startPosition, subPageId);
        readerNotePage.setLoaded(true);
        return readerNotePage;
    }

    public ReaderNotePage addPage(final PageRange range, int subPageIndex) {
        String subPageId = ShapeUtils.generateUniqueId();
        addIndexEntry(range, subPageIndex, subPageId);
        ReaderNotePage readerNotePage = ensureDataEntry(range.startPosition, subPageId);
        readerNotePage.setLoaded(true);
        return readerNotePage;
    }

    public String clearPage(final Context context, final PageRange range, final int subPageIndex) {
        final String subPageUniqueId = getPageIndex().getSubPageUniqueId(range, subPageIndex);
        if (subPageUniqueId == null) {
            return null;
        }
        final ReaderNotePage notePage = getPageMap().get(subPageUniqueId);
        if (notePage == null) {
            return null;
        }
        notePage.clear(true);
        notePage.savePage(context);
        return subPageUniqueId;
    }

    public String removePage(final Context context, final PageRange range, final int subPageIndex) {
        String subPageId = getPageIndex().removeSubPage(range, subPageIndex);
        if (subPageId == null) {
            return null;
        }
        getPageMap().remove(subPageId);
        return subPageId;
    }

    private void createIndexEntry(final PageRange range, int index, final String subPageUniqueId) {
        getPageIndex().set(range, index, subPageUniqueId);
    }

    private void addIndexEntry(final PageRange range, int index, final String subPageUniqueId) {
        getPageIndex().add(range, index, subPageUniqueId);
    }

    private ReaderNotePage ensureDataEntry(final String pagePosition, final String subPageUniqueId) {
        if (!getPageMap().containsKey(subPageUniqueId)) {
            final ReaderNotePage notePage = new ReaderNotePage(getDocumentUniqueId(), pagePosition, subPageUniqueId);
            getPageMap().put(subPageUniqueId, notePage);
        }
        return getPageMap().get(subPageUniqueId);
    }

    public String getSubPageUniqueId(final PageRange range, int subPageIndex) {
        return getPageIndex().getSubPageUniqueId(range, subPageIndex);
    }

    public boolean loadPages(final Context context, final List<PageInfo> visiblePages) {
        for(PageInfo pageInfo: visiblePages) {
            loadPage(context, pageInfo.getRange(), pageInfo.getSubPage());
        }
        return true;
    }

    public ReaderNotePage loadPage(final Context context, final PageRange range, int subPageIndex) {
        ReaderNotePage notePage;
        final String pageUniqueId = getSubPageUniqueId(range, subPageIndex);
        if (StringUtils.isNullOrEmpty(pageUniqueId)) {
            return null;
        }
        notePage = ensureDataEntry(range.startPosition, pageUniqueId);
        if (notePage != null && notePage.isLoaded()) {
            return notePage;
        }
        notePage.loadPage(context);
        return notePage;
    }

    public ReaderNotePage ensurePageExist(final Context context, final String pagePosition, String subPageId) {
        return ensureDataEntry(pagePosition, subPageId);
    }

    public ReaderNotePage ensurePageExist(final Context context, final PageRange range, int subPageIndex) {
        final ReaderNotePage readerNotePage = loadPage(context, range, subPageIndex);
        if (readerNotePage != null) {
            return readerNotePage;
        }
        return createPage(range, subPageIndex);
    }

    public final List<String> getPageList() {
        return pageIndex.nameList();
    }

    public final List<PageInfo> getNoEmptyPageList(final Context context) {
        List<PageInfo> pageList = new ArrayList<>();
        for (String page : getPageList()) {
            int count = getSubPageCount(new PageRange(page, page));
            for (int i = 0; i < count; i++) {
                String pageUniqueId = getSubPageUniqueId(new PageRange(page, page), i);
                ReaderNotePage notePage = pageMap.get(pageUniqueId);
                if (notePage == null) {
                    notePage = loadPage(context, new PageRange(page, page), i);
                }
                if (notePage == null) {
                    continue;
                }
                List<Shape> shapeList = notePage.getShapeList();
                if (shapeList == null || shapeList.size() <= 0) {
                    continue;
                }
                PageInfo pageInfo = new PageInfo(page, page, page, -1, -1);
                pageInfo.setSubPage(i);
                pageList.add(pageInfo);
            }
        }
        return pageList;
    }
}
