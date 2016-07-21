package com.onyx.kreader.common;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.onyx.kreader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.dataprovider.Annotation;
import com.onyx.kreader.dataprovider.AnnotationProvider;
import com.onyx.kreader.dataprovider.Bookmark;
import com.onyx.kreader.dataprovider.BookmarkProvider;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.wrapper.Reader;

import java.util.*;

/**
 * Created by zhuzeng on 6/7/16.
 */
public class ReaderUserDataInfo {

    private final static String SEARCH_TAG = "search";
    private final static String HIGHLIGHT_TAG = "highlight";
    private Map<String, List<ReaderSelection>> selectionMap = new HashMap<>();

    private String documentPath;

    private ReaderDocumentTableOfContent toc;
    private Map<String, Bookmark> bookmarkMap = new HashMap<>();
    private Map<String, List<Annotation>> annotationMap = new HashMap<>();
    private Map<String, List<PageAnnotation>> pageAnnotationMap = new HashMap<>();

    public void saveDocumentPath(final String path) {
        Debug.d("saveDocumentPath: " + path);
        documentPath = path;
    }

    public String getDocumentPath() {
        return documentPath;
    }

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
        Debug.d("saveHighlightResult: " + JSON.toJSONString(selection));
        selectionMap.put(HIGHLIGHT_TAG, Arrays.asList(new ReaderSelection[] { selection }));
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

    public boolean loadAnnotations(final Context context, final Reader reader) {
        final List<Annotation> annotations = AnnotationProvider.loadAnnotations(reader.getPlugin().displayName(), reader.getDocumentMd5());
        if (annotations != null && annotations.size() > 0) {
            for (Annotation annotation : annotations) {
                if (annotationMap.get(annotation.getPosition()) == null) {
                    annotationMap.put(annotation.getPosition(), new ArrayList<Annotation>());
                }
                annotationMap.get(annotation.getPosition()).add(annotation);
            }
        }
        return false;
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
            final List<Annotation> annotations = AnnotationProvider.loadAnnotations(reader.getPlugin().displayName(), reader.getDocumentMd5(), pageInfo.getName());
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
        return bookmarkMap.get(pageInfo.getName()) != null;
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
            final Bookmark bookmark = BookmarkProvider.loadBookmark(reader.getPlugin().displayName(), reader.getDocumentMd5(), pageInfo.getName());
            bookmarkMap.put(pageInfo.getName(), bookmark);
        }
        return true;
    }

    public boolean loadBookmarks(final Context context, final Reader reader) {
        List<Bookmark> bookmarks = BookmarkProvider.loadBookmarks(reader.getPlugin().displayName(), reader.getDocumentMd5());
        if (bookmarks != null) {
            for (Bookmark bookmark : bookmarks) {
                bookmarkMap.put(bookmark.getPosition(), bookmark);
            }
        }
        return true;
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

}
