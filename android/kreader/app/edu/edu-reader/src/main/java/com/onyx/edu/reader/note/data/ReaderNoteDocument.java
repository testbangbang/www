package com.onyx.edu.reader.note.data;

import android.content.Context;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.*;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;
import com.onyx.edu.reader.note.model.ReaderNoteDocumentModel;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuzeng on 9/16/16.
 */
public class ReaderNoteDocument {

    private String documentUniqueId;
    private String parentUniqueId;
    private ReaderNotePageNameMap pageIndex = new ReaderNotePageNameMap();
    private LinkedHashMap<String, ReaderNotePage> pageMap = new LinkedHashMap<>();
    private NoteDrawingArgs noteDrawingArgs = new NoteDrawingArgs();
    private int reviewStatus;
    private int reviewRevision;
    private boolean isOpen = false;

    public void open(final Context context,
                     final String uniqueId,
                     final String parentLibraryUniqueId) {
        if (StringUtils.isNullOrEmpty(uniqueId)) {
            return;
        }
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
        reviewRevision = 0;
        reviewStatus = 0;
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

    public String getReaderDocumentTitle() {
        return "title";
    }

    public void save(final Context context) {
        save(context, getReaderDocumentTitle());
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
        noteModel.setReviewStatus(reviewStatus);
        noteModel.setReviewRevision(reviewRevision);
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
        setupReviewRevision(documentModel);
        setupReviewStatus(documentModel);
    }

    private void setupPageIndex(final ReaderNoteDocumentModel noteModel) {
        pageIndex = loadPageIndex(noteModel);
    }

    private void setupReviewStatus(final ReaderNoteDocumentModel noteModel) {
        if (noteModel != null) {
            reviewStatus = noteModel.getReviewStatus();
        }
    }

    private void setupReviewRevision(final ReaderNoteDocumentModel noteModel) {
        if (noteModel != null) {
            reviewRevision = noteModel.getReviewRevision();
        }
    }

    public boolean isLock() {
        return reviewStatus == ReaderShapeFactory.NOTE_DOCUMENT_LOCK_STATE;
    }

    public void lockNoteDocument() {
        reviewStatus = ReaderShapeFactory.NOTE_DOCUMENT_LOCK_STATE;
    }

    public void resetNoteDocumentState() {
        reviewStatus = ReaderShapeFactory.NOTE_DOCUMENT_NORMAL_STATE;
    }

    public int getReviewRevision() {
        return reviewRevision;
    }

    public void setReviewRevision(int reviewRevision) {
        this.reviewRevision = reviewRevision;
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

    public void addReviewDataPageMap(ReaderNotePageNameMap pageNameMap) {
        for (Map.Entry<String, List<String>> stringListEntry : pageNameMap.getData().entrySet()) {
            String pageName = stringListEntry.getKey();
            List<String> list = stringListEntry.getValue();
            for (String s : list) {
                List<String> subPageUniqueIds = pageIndex.getPageList(pageName, true);
                String subPageUniqueId = s;
                // add when has not subPageUniqueId
                if (subPageUniqueIds.size() == 0) {
                    pageIndex.add(pageName, s);
                }else {
                    // use already existing subPageUniqueId
                    subPageUniqueId = subPageUniqueIds.get(0);
                }

                ReaderNotePage notePage = pageMap.get(subPageUniqueId);
                if (notePage == null) {
                    notePage = ensureDataEntry(pageName, s);
                }
                notePage.setLoaded(false);
            }
        }
    }

    private HashMap<String, ReaderNotePage> getPageMap() {
        return pageMap;
    }

    public int getSubPageCount(final String pageName) {
        List<String> list = getPageIndex().getPageList(pageName, false);
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public ReaderNotePage createPage(final String pageName, int subPageIndex) {
        String pageUniqueId = ShapeUtils.generateUniqueId();
        createIndexEntry(pageName, subPageIndex, pageUniqueId);
        ReaderNotePage readerNotePage = ensureDataEntry(pageName, pageUniqueId);
        readerNotePage.setLoaded(true);
        return readerNotePage;
    }

    public String clearPage(final Context context, final String pageName, final int subPageIndex, boolean lockShapeByRevision) {
        final List<String> list = getPageIndex().getPageList(pageName, false);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        if (subPageIndex < 0 || subPageIndex >= list.size()) {
            return null;
        }
        final String subPageUniqueId = list.get(subPageIndex);
        final ReaderNotePage notePage = getPageMap().get(subPageUniqueId);
        if (notePage == null) {
            return null;
        }
        notePage.clear(true, false, lockShapeByRevision, getReviewRevision());
        notePage.savePage(context);
        notePage.setLoaded(false);
        return subPageUniqueId;
    }

    public String removePage(final Context context, final String pageName, final int subPageIndex) {
        final List<String> list = getPageIndex().getPageList(pageName, false);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        if (subPageIndex < 0 || subPageIndex >= list.size()) {
            return null;
        }
        final String subPageUniqueId = list.get(subPageIndex);
        getPageMap().remove(subPageUniqueId);
        getPageIndex().remove(pageName, subPageUniqueId);
        if (list == null || list.size() == 0) {
            getPageIndex().remove(pageName);
        }
        return subPageUniqueId;
    }

    private void createIndexEntry(final String pageName, int index, final String pageUniqueId) {
        getPageIndex().add(pageName, index, pageUniqueId);
    }

    private ReaderNotePage ensureDataEntry(final String pageName, final String pageUniqueId) {
        if (!getPageMap().containsKey(pageUniqueId)) {
            final ReaderNotePage notePage = new ReaderNotePage(getDocumentUniqueId(), pageName, pageUniqueId);
            getPageMap().put(pageUniqueId, notePage);
        }
        return getPageMap().get(pageUniqueId);
    }

    public String getPageUniqueId(final String pageName, int index) {
        final List<String> list = getPageIndex().getPageList(pageName, false);
        if (list == null) {
            return null;
        }
        if (index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    public boolean loadPages(final Context context, final List<PageInfo> visiblePages) {
        for(PageInfo pageInfo: visiblePages) {
            loadPage(context, pageInfo.getName(), 0);
        }
        return true;
    }

    public ReaderNotePage loadPage(final Context context, final String pageName, int subPageIndex) {
        ReaderNotePage notePage;
        final String pageUniqueId = getPageUniqueId(pageName, subPageIndex);
        if (StringUtils.isNullOrEmpty(pageUniqueId)) {
            return null;
        }
        notePage = ensureDataEntry(pageName, pageUniqueId);
        if (notePage != null && notePage.isLoaded()) {
            return notePage;
        }
        notePage.loadPage(context);
        return notePage;
    }

    public ReaderNotePage ensurePageExist(final Context context, final String pageName, int subPageIndex) {
        final ReaderNotePage readerNotePage = loadPage(context, pageName, subPageIndex);
        if (readerNotePage != null) {
            return readerNotePage;
        }
        return createPage(pageName, subPageIndex);
    }

    public final List<String> getPageList() {
        return pageIndex.nameList();
    }

    public final List<String> getNoEmptyPageList(final Context context) {
        List<String> pageList = new ArrayList<>();
        for (String pageName : getPageList()) {
            String pageUniqueId = getPageUniqueId(pageName, 0);
            ReaderNotePage notePage = pageMap.get(pageUniqueId);
            if (notePage == null) {
                notePage = loadPage(context, pageName, 0);
            }
            if (notePage == null) {
                continue;
            }
            List<Shape> shapeList = notePage.getShapeList();
            if (shapeList != null && shapeList.size() > 0) {
                pageList.add(pageName);
            }
        }
        return pageList;
    }
}
