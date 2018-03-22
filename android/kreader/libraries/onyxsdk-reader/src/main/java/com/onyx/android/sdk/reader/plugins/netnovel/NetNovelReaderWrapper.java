package com.onyx.android.sdk.reader.plugins.netnovel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderCallback;
import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.android.sdk.reader.api.ReaderDocumentOptions;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.host.impl.ReaderSelectionImpl;
import com.onyx.android.sdk.reader.host.impl.ReaderTextSplitterImpl;
import com.onyx.android.sdk.reader.plugins.alreader.AlReaderWrapper;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joy on 3/19/18.
 */

public class NetNovelReaderWrapper {

    private Context context;
    private ReaderPluginOptions pluginOptions;
    private AlReaderWrapper alReaderWrapper;
    private ReaderCallback callback;
    private int viewWidth, viewHeight;
    private ReaderTextStyle style;

    private boolean aborted = false;

    private File bookDirectory;
    private ReaderDocumentOptions documentOptions;

    private NetNovelBook book;
    private String bookId;

    List<NetNovelChapter> chapterList;
    private HashMap<String, NetNovelChapter> chapterMap = new HashMap<>();
    private HashMap<String, Integer> chapterIndexMap = new HashMap<>();
    private String currentChapterId;

    public NetNovelReaderWrapper(final Context context, final ReaderPluginOptions pluginOptions) {
        this.context = context;
        this.pluginOptions = pluginOptions;
        alReaderWrapper = new AlReaderWrapper(context, pluginOptions);
    }

    public void setAborted(boolean abort) {
        aborted = abort;
        alReaderWrapper.setAborted(abort);
    }

    public void abortBookLoading() {
        aborted = true;
        alReaderWrapper.abortBookLoading();
    }

    public boolean openDocument(final String path,  final ReaderDocumentOptions documentOptions) throws ReaderException {
        bookDirectory = new File(path).getParentFile();
        this.documentOptions = documentOptions;

        String content = FileUtils.readContentOfFile(path);
        if (StringUtils.isNullOrEmpty(content)) {
            return false;
        }

        book = NetNovelBook.createFromJSON(content);
        if (book == null) {
            return false;
        }

        bookId = FileUtils.getBaseName(path);

        chapterList = book.getChapterFlattenList();
        if (chapterList.size() <= 0) {
            return false;
        }

        for (int i = 0; i < chapterList.size(); i++) {
            NetNovelChapter chapter = chapterList.get(i);
            chapterMap.put(chapter.id, chapter);
            chapterIndexMap.put(chapter.id, i);
        }

        return openChapter(chapterList.get(0).id, true);
    }

    public void close() {
        alReaderWrapper.closeDocument();
    }

    public void setBookCallback(final ReaderCallback callback) {
        this.callback = callback;
        alReaderWrapper.setBookCallback(callback);
    }

    public void setViewSize(int viewWidth, int viewHeight) {
        alReaderWrapper.setViewSize(viewWidth, viewHeight);
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    public ReaderTextStyle getStyle() {
        return alReaderWrapper.getStyle();
    }

    public void setStyle(ReaderTextStyle style) {
        this.style = style;
        alReaderWrapper.setStyle(style);
    }

    public int getTotalPage() {
        return chapterMap.size();
    }

    public float getProgress() {
        return (getCurrentChapterIndex() + 1) / (float)chapterList.size() * 100;
    }

    public String getInitPosition() {
        return new NetNovelLocation(bookId, chapterList.get(0).id, 0, 0).toJson();
    }

    public boolean gotoPosition(String position) throws ReaderException {
        NetNovelLocation location = NetNovelLocation.createFromJSON(position);
        return gotoLocation(location);
    }

    public boolean gotoPage(int page) throws ReaderException {
        if (page < 0 || page >= chapterList.size()) {
            return false;
        }

        NetNovelChapter chapter = chapterList.get(page);
        if (!openChapter(chapter.id, false)) {
            return false;
        }

        return gotoPositionInChapter(0);
    }

    public boolean nextPage() throws ReaderException {
        if (!alReaderWrapper.isLastPage()) {
            return alReaderWrapper.nextPage();
        }

        return nextChapter();
    }

    public boolean prevPage() throws ReaderException {
        if (!alReaderWrapper.isFirstPage()) {
            return alReaderWrapper.prevPage();
        }

        if (!previousChapter()) {
            return false;
        }

        while (!alReaderWrapper.isLastPage()) {
            if (!alReaderWrapper.nextPage()) {
                return false;
            }
        }
        return true;
    }

    public void draw(Bitmap bitmap, int width, int height) {
        alReaderWrapper.draw(bitmap, width, height);
    }

    public int getScreenStartPage() {
        return getCurrentChapterIndex();
    }

    public int getScreenEndPage() {
        return getCurrentChapterIndex();
    }

    public boolean isFirstPage() {
        return isFirstChapter() && alReaderWrapper.isFirstPage();
    }

    public boolean isLastPage() {
        return isLastChapter() && alReaderWrapper.isLastPage();
    }

    public String getPositionOfPageNumber(int pageNumber) {
        return new NetNovelLocation(bookId, chapterList.get(pageNumber).id, pageNumber, 0).toJson();
    }

    public int getPageNumberOfPosition(String position) {
        NetNovelLocation location = NetNovelLocation.createFromJSON(position);
        return location.chapterIndex;
    }

    public String getScreenStartPosition() {
        return getScreenStartLocation().toJson();
    }

    public String getScreenEndPosition() {
        return new NetNovelLocation(bookId, currentChapterId, getCurrentChapterIndex(), alReaderWrapper.getScreenEndPosition()).toJson();
    }

    public int comparePosition(String pos1, String pos2) {
        NetNovelLocation loc1 = NetNovelLocation.createFromJSON(pos1);
        NetNovelLocation loc2 = NetNovelLocation.createFromJSON(pos2);

        int idx1 = chapterIndexMap.get(loc1.chapterId);
        int idx2 = chapterIndexMap.get(loc2.chapterId);

        int compare = idx1 - idx2;
        if (compare != 0) {
            return compare;
        }

        return loc1.positionInChapter - loc2.positionInChapter;
    }

    public boolean readTableOfContent(final ReaderDocumentTableOfContent toc) {
        for (int i = 0; i < chapterList.size(); i++) {
            NetNovelChapter ch = chapterList.get(i);
            ReaderDocumentTableOfContentEntry entry = ReaderDocumentTableOfContentEntry.createEntry(ch.title, i,
                    new NetNovelLocation(bookId, ch.id, i, 0).toJson());
            toc.getRootEntry().addChildEntry(entry);
        }
        return true;
    }

    public ReaderSelection selectWordOnScreen(PointF point, ReaderTextSplitterImpl readerTextSplitter) {
        return alReaderWrapper.selectWordOnScreen(point, readerTextSplitter);
    }

    public ReaderSelection selectTextOnScreen(PointF start, PointF end) {
        return alReaderWrapper.selectTextOnScreen(start, end);
    }

    public ReaderSelection selectTextOnScreen(String startPosition, String endPosition) {
        NetNovelLocation start = NetNovelLocation.createFromJSON(startPosition);
        NetNovelLocation end = NetNovelLocation.createFromJSON(endPosition);
        return alReaderWrapper.selectTextOnScreen(start.positionInChapter, end.positionInChapter);
    }

    public boolean search(final String text, final List<ReaderSelection> list) {
        NetNovelLocation locationBackup = getScreenStartLocation();

        try {
            for (int i = 0; i < chapterList.size(); i++) {
                if (aborted) {
                    return true;
                }

                NetNovelChapter ch = chapterList.get(i);
                if (!isChapterReady(ch.id)) {
                    break;
                }

                try {
                    if (!openChapter(ch.id, false)) {
                        return false;
                    }
                    ArrayList<ReaderSelection> l = new ArrayList<>();
                    alReaderWrapper.search(text, l);
                    for (ReaderSelection selection : l) {
                        list.add(new ReaderSelectionImpl(selection.getRectangles(),
                                new NetNovelLocation(bookId, ch.id, i, Integer.parseInt(selection.getEndPosition())).toJson(),
                                selection.getLeftText(),
                                selection.getPageName(),
                                new NetNovelLocation(bookId, ch.id, i, Integer.parseInt(selection.getPagePosition())).toJson(),
                                selection.getRightText(),
                                new NetNovelLocation(bookId, ch.id, i, Integer.parseInt(selection.getStartPosition())).toJson(),
                                selection.getText()));
                    }
                } catch (ReaderException e) {
                    return false;
                }
            }
            return true;
        } finally {
            try {
                gotoLocation(locationBackup);
            } catch (ReaderException e) {
                e.printStackTrace();
            }
        }
    }

    private NetNovelLocation getScreenStartLocation() {
        return new NetNovelLocation(bookId, currentChapterId, getCurrentChapterIndex(), alReaderWrapper.getScreenStartPosition());
    }

    private boolean gotoLocation(NetNovelLocation location) throws ReaderException {
        if (!openChapter(location.chapterId, false)) {
            return false;
        }

        return gotoPositionInChapter(location.positionInChapter);
    }

    private boolean isChapterReady(String chapterId) {
        String path = new File(bookDirectory, chapterId).getAbsolutePath();
        return FileUtils.fileExist(path);
    }

    private boolean openChapter(String chapterId, boolean enableCallback) throws ReaderException {
        if (!isChapterReady(chapterId)) {
            NetNovelLocation location = new NetNovelLocation(bookId, chapterId, -1, 0);
            throw ReaderException.netNovelChapterNotFound(location.toJson());
        }

        if (currentChapterId != null) {
            if (currentChapterId.compareTo(chapterId) == 0) {
                return true;
            }
            alReaderWrapper.closeDocument();
        }

        String path = new File(bookDirectory, chapterId).getAbsolutePath();

        alReaderWrapper = new AlReaderWrapper(context, pluginOptions);
        if (enableCallback) {
            alReaderWrapper.setBookCallback(callback);
        }
        alReaderWrapper.setViewSize(viewWidth, viewHeight);
        if (style != null) {
            alReaderWrapper.setStyle(style);
        }
        if (alReaderWrapper.openDocument(new AlFileDecryptNetNovel(context, path), createDocumentOptions(0)) == AlReaderWrapper.NO_ERROR) {
            currentChapterId = chapterId;
            return true;
        }

        return false;
    }

    private ReaderDocumentOptions createDocumentOptions(final int initPosition) {
        return new ReaderDocumentOptions() {
            @Override
            public String getDocumentPassword() {
                return documentOptions.getDocumentPassword();
            }

            @Override
            public String getCompressedPassword() {
                return documentOptions.getCompressedPassword();
            }

            @Override
            public String getLanguage() {
                return documentOptions.getLanguage();
            }

            @Override
            public int getCodePage() {
                return documentOptions.getCodePage();
            }

            @Override
            public int getCodePageFallback() {
                return documentOptions.getCodePageFallback();
            }

            @Override
            public ReaderChineseConvertType getChineseConvertType() {
                return documentOptions.getChineseConvertType();
            }

            @Override
            public boolean isCustomFormEnabled() {
                return documentOptions.isCustomFormEnabled();
            }

            @Override
            public int getReadPosition() {
                return initPosition;
            }

            @Override
            public String getDocumentKey() {
                return documentOptions.getDocumentKey();
            }

            @Override
            public String getDocumentDeviceUUID() {
                return documentOptions.getDocumentDeviceUUID();
            }

            @Override
            public String getDocumentRandom() {
                return documentOptions.getDocumentRandom();
            }
        };
    }

    private boolean gotoPositionInChapter(int position) {
        return alReaderWrapper.gotoPosition(position);
    }

    private int getCurrentChapterIndex() {
        return chapterIndexMap.get(currentChapterId);
    }

    private boolean isFirstChapter() {
        return getCurrentChapterIndex() == 0;
    }

    private boolean isLastChapter() {
        return getCurrentChapterIndex() == chapterList.size() - 1;
    }

    private boolean nextChapter() throws ReaderException {
        if (isLastChapter()) {
            return false;
        }

        return openChapter(chapterList.get(getCurrentChapterIndex() + 1).id, false);
    }

    private boolean previousChapter() throws ReaderException {
        if (isFirstChapter()) {
            return false;
        }
        return openChapter(chapterList.get(getCurrentChapterIndex() - 1).id, false);
    }
}
