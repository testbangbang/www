package com.onyx.kreader.common;

import android.content.Context;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.dataprovider.Annotation;
import com.onyx.kreader.dataprovider.AnnotationProvider;
import com.onyx.kreader.dataprovider.Bookmark;
import com.onyx.kreader.dataprovider.BookmarkProvider;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.host.wrapper.Reader;

import java.util.*;

/**
 * Created by zhuzeng on 6/7/16.
 */
public class ReaderUserDataInfo {

    private final static String SEARCH_TAG = "search";
    private final static String HIGHLIGHT_TAG = "highlight";
    private Map<String, List<ReaderSelection>> selectionMap = new HashMap<String, List<ReaderSelection>>();

    private Map<String, Bookmark> bookmarkMap = new HashMap<>();
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

    public boolean loadAnnotations(final Context context, final Reader reader, final List<PageInfo> visiblePages) {
        for(PageInfo pageInfo: visiblePages) {
            final List<Annotation> annotations = AnnotationProvider.loadAnnotations(context, reader.getDocumentMd5(), pageInfo.getName());
            annotationMap.put(pageInfo.getName(), annotations);
        }
        return true;
    }

    public boolean hasBookmark(final PageInfo pageInfo) {
        return bookmarkMap.get(pageInfo.getName()) != null;
    }

    public Bookmark getBookmark(final PageInfo pageInfo) {
        return bookmarkMap.get(pageInfo.getName());
    }

    public boolean loadBookmarks(Context context, final Reader reader, final List<PageInfo> visiblePages) {
        for(PageInfo pageInfo: visiblePages) {
            final Bookmark bookmark = BookmarkProvider.loadBookmark(reader.getDocumentMd5(), reader.getPlugin().displayName(), pageInfo.getName());
            bookmarkMap.put(pageInfo.getName(), bookmark);
        }
        return true;
    }

}
