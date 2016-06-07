package com.onyx.kreader.common;

import android.content.Context;
import android.graphics.RectF;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.dataprovider.Annotation;
import com.onyx.kreader.dataprovider.AnnotationProvider;
import com.onyx.kreader.dataprovider.Bookmark;
import com.onyx.kreader.dataprovider.BookmarkProvider;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.options.ReaderConstants;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.scribble.data.ShapeDataProvider;
import com.onyx.kreader.scribble.data.ShapeModel;
import com.onyx.kreader.scribble.data.ShapePage;
import com.onyx.kreader.scribble.shape.Shape;
import com.onyx.kreader.scribble.shape.ShapeFactory;

import java.util.*;

/**
 * Created by zhuzeng on 6/7/16.
 */
public class ReaderUserDataInfo {

    private final static String SEARCH_TAG = "search";
    private final static String HIGHLIGHT_TAG = "highlight";
    private Map<String, List<ReaderSelection>> selectionMap = new HashMap<String, List<ReaderSelection>>();

    private Map<String, ShapePage> shapePageMap = new HashMap<String, ShapePage>();
    public boolean canUndoShape;
    public boolean canRedoShape;

    private Map<String, List<Bookmark>> bookmarkMap = new HashMap<String, List<Bookmark>>();
    private Map<String, List<Annotation>> annotationMap = new HashMap<String, List<Annotation>>();

    public boolean hasSearchResults() {
        List<ReaderSelection> list = getSearchResults();
        return list != null && list.size() > 0;
    }

    public List<ReaderSelection> getSearchResults() {
        return selectionMap.get(SEARCH_TAG);
    }

    public void saveSearchResults(List<ReaderSelection> list) {
        selectionMap.put(SEARCH_TAG, list);
    }

    public boolean hasHighlightResult() {
        return getHighlightResult() != null;
    }

    public ReaderSelection getHighlightResult() {
        List<ReaderSelection> list = selectionMap.get(HIGHLIGHT_TAG);
        if (list == null || list.size() <= 0) {
            return null;
        }
        return list.get(0);
    }

    public void saveHighlightResult(ReaderSelection selection) {
        selectionMap.put(HIGHLIGHT_TAG, Arrays.asList(new ReaderSelection[] { selection }));
    }

    public boolean loadUserShape(final Context context, final Reader reader, final List<PageInfo> visiblePages) {
        for(PageInfo pageInfo: visiblePages) {
            final ShapePage shapePage = ShapePage.createPage(context, reader.getDocumentMd5(), pageInfo.getName(), null);
            final List<ShapeModel> modelList = ShapeDataProvider.loadShapeList(context, reader.getDocumentMd5(), pageInfo.getName(), null);
            for(ShapeModel model : modelList) {
                shapePage.addShapeFromModel(ShapeFactory.shapeFromModel(model));
            }
            shapePageMap.put(pageInfo.getName(), shapePage);
        }
        return true;
    }

    public boolean loadAnnotations(final Context context, final Reader reader, final List<PageInfo> visiblePages) {
        for(PageInfo pageInfo: visiblePages) {
            final List<Annotation> annotations = AnnotationProvider.loadAnnotations(context, reader.getDocumentMd5(), pageInfo.getName());
            annotationMap.put(pageInfo.getName(), annotations);
        }
        return true;
    }

    public boolean loadBookmarks(final Context context, final Reader reader, final List<PageInfo> visiblePages) {
        for(PageInfo pageInfo: visiblePages) {
            final List<Bookmark> bookmarks = BookmarkProvider.loadBookmarks(context, reader.getDocumentMd5(), pageInfo.getName());
            bookmarkMap.put(pageInfo.getName(), bookmarks);
        }
        return true;
    }
}
