package com.onyx.kreader.common;

import android.content.Context;
import android.graphics.PointF;

import com.onyx.android.sdk.data.model.*;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.provider.SearchHistoryProvider;
import com.onyx.kreader.api.ReaderDocumentMetadata;
import com.onyx.kreader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.wrapper.Reader;
import com.raizlabs.android.dbflow.sql.language.OrderBy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuzeng on 6/7/16.
 */
public class ReaderUserDataInfo {
    private List<ReaderSelection> searchResult = new ArrayList<>();
    private ReaderSelection highlightResult = null;
    private boolean wordSelected = false;
    private PointF touchPoint;

    private String documentPath;
    private ReaderDocumentMetadata documentMetadata;

    private ReaderDocumentTableOfContent toc;
    private Map<String, Bookmark> bookmarkMap = new LinkedHashMap<>();
    private Map<String, List<Annotation>> annotationMap = new LinkedHashMap<>();
    private Map<String, List<PageAnnotation>> pageAnnotationMap = new HashMap<>();
    private List<SearchHistory> searchHistoryList = new ArrayList<>();

    public void setDocumentPath(final String path) {
        documentPath = path;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentMetadata(ReaderDocumentMetadata documentMetadata) {
        this.documentMetadata = documentMetadata;
    }

    public ReaderDocumentMetadata getDocumentMetadata() {
        return documentMetadata;
    }

    public boolean hasSearchResults() {
        List<ReaderSelection> list = getSearchResults();
        return list != null && list.size() > 0;
    }

    public List<ReaderSelection> getSearchResults() {
        return searchResult;
    }

    public void saveSearchResults(List<ReaderSelection> list) {
        searchResult = list;
    }

    public boolean hasHighlightResult() {
        return getHighlightResult() != null;
    }

    public ReaderSelection getHighlightResult() {
        return highlightResult;
    }

    public void saveHighlightResult(ReaderSelection selection) {
        highlightResult = selection;
    }

    public void setTableOfContent(ReaderDocumentTableOfContent toc) {
        this.toc = toc;
    }

    public ReaderDocumentTableOfContent getTableOfContent() {
        return toc;
    }

    public List<Annotation> getAnnotations() {
        ArrayList<Annotation> list = new ArrayList<>();
        Collection<List<Annotation>> values = annotationMap.values();
        for (List<Annotation> l : values) {
            list.addAll(l);
        }
        return list;
    }

    public boolean loadDocumentTableOfContent(final Context context, final Reader reader) {
        toc = new ReaderDocumentTableOfContent();
        return reader.getDocument().readTableOfContent(toc);
    }

    public boolean loadDocumentAnnotations(final Context context, final Reader reader) {
        final List<Annotation> annotations = DataProviderManager.getDataProvider().loadAnnotations(
                reader.getPlugin().displayName(),
                reader.getDocumentMd5(),
                OrderBy.fromProperty(Annotation_Table.pageNumber).ascending());
        if (annotations != null && annotations.size() > 0) {
            for (Annotation annotation : annotations) {
                if (annotationMap.get(annotation.getPosition()) == null) {
                    annotationMap.put(annotation.getPosition(), new ArrayList<Annotation>());
                }
                annotationMap.get(annotation.getPosition()).add(annotation);
            }
        }
        return true;
    }

    public boolean hasPageAnnotations(final PageInfo pageInfo) {
        List<PageAnnotation> list = getPageAnnotations(pageInfo);
        return list != null && list.size() > 0;
    }

    public List<PageAnnotation> getPageAnnotations(final PageInfo pageInfo) {
        return pageAnnotationMap.get(pageInfo.getName());
    }

    public boolean loadPageAnnotations(final Context context, final Reader reader, final List<PageInfo> visiblePages) {
        for(PageInfo pageInfo: visiblePages) {
            final List<Annotation> annotations = DataProviderManager.getDataProvider().loadAnnotations(
                    reader.getPlugin().displayName(),
                    reader.getDocumentMd5(),
                    pageInfo.getName(),
                    OrderBy.fromProperty(Annotation_Table.pageNumber).ascending());
            if (annotations != null && annotations.size() > 0) {
                List<PageAnnotation> list = new ArrayList<>();
                for (Annotation annotation : annotations) {
                    list.add(new PageAnnotation(pageInfo, annotation));
                }
                pageAnnotationMap.put(pageInfo.getName(), list);
            }
        }
        return true;
    }

    public boolean hasBookmark(final PageInfo pageInfo) {
        return bookmarkMap.containsKey(pageInfo.getName());
    }

    public Bookmark getBookmark(final PageInfo pageInfo) {
        return bookmarkMap.get(pageInfo.getName());
    }

    public List<Bookmark> getBookmarks() {
        ArrayList<Bookmark> list = new ArrayList<>();
        list.addAll(bookmarkMap.values());
        return list;
    }

    public boolean loadBookmarks(final Context context, final Reader reader, final List<PageInfo> visiblePages) {
        for(PageInfo pageInfo: visiblePages) {
            final Bookmark bookmark = DataProviderManager.getDataProvider().loadBookmark(reader.getPlugin().displayName(), reader.getDocumentMd5(), pageInfo.getName());
            if (bookmark != null) {
                bookmarkMap.put(pageInfo.getName(), bookmark);
            }
        }
        return true;
    }

    public boolean loadDocumentBookmarks(final Context context, final Reader reader) {
        List<Bookmark> bookmarks = DataProviderManager.getDataProvider().loadBookmarks(
                reader.getPlugin().displayName(),
                reader.getDocumentMd5(),
                OrderBy.fromProperty(Bookmark_Table.pageNumber).ascending());
        if (bookmarks != null) {
            for (Bookmark bookmark : bookmarks) {
                bookmarkMap.put(bookmark.getPosition(), bookmark);
            }
        }
        return true;
    }

    public boolean loadSearchHistory(final Context context, final Reader reader, int count){
        searchHistoryList = SearchHistoryProvider.getLatestSearchHistory(reader.getDocumentMd5(), count);
        return true;
    }

    public List<SearchHistory> getSearchHistoryList() {
        return searchHistoryList;
    }

    private Annotation translateToScreen(PageInfo pageInfo, Annotation annotation) {
        for (int i = 0; i < annotation.getRectangles().size(); i++) {
            PageUtils.translate(pageInfo.getDisplayRect().left,
                    pageInfo.getDisplayRect().top,
                    pageInfo.getActualScale(),
                    annotation.getRectangles().get(i));
        }
        return annotation;
    }

    public PointF getTouchPoint() {
        return touchPoint;
    }

    public void setTouchPoint(PointF touchPoint) {
        this.touchPoint = touchPoint;
    }

    public boolean isWordSelected() {
        return wordSelected;
    }

    private void setWordSelected(boolean wordSelected) {
        this.wordSelected = wordSelected;
    }
}
