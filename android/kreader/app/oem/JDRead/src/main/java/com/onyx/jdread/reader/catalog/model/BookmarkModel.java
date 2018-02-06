package com.onyx.jdread.reader.catalog.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.common.ReaderPageInfoFormat;
import com.onyx.jdread.reader.utils.ReaderViewUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by huxiaomao on 2018/1/10.
 */

public class BookmarkModel extends BaseObservable {
    private ObservableField<String> chapter = new ObservableField<>();
    private ObservableField<String> data = new ObservableField<>();
    private ObservableField<String> content = new ObservableField<>();
    private ObservableField<String> readProgress = new ObservableField<>();
    private String position;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public ObservableField<String> getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter.set(chapter);
    }

    public ObservableField<String> getData() {
        return data;
    }

    public void setData(String data) {
        this.data.set(data);
    }

    public ObservableField<String> getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public ObservableField<String> getReadProgress() {
        return readProgress;
    }

    public void setReadProgress(String readProgress) {
        this.readProgress.set(readProgress);
    }

    public static BookmarkModel convertObject(ReaderDocumentTableOfContent readerDocumentTableOfContent,Bookmark bookmark,int totalPage){
        BookmarkModel bookmarkModel = new BookmarkModel();
        String title = "";
        if (readerDocumentTableOfContent != null && hasChildren(readerDocumentTableOfContent.getRootEntry())) {
            ReaderDocumentTableOfContentEntry entry = locateEntry(readerDocumentTableOfContent.getRootEntry().getChildren(), PagePositionUtils.getPosition(bookmark.getPosition()));
            title = entry.getTitle();
        }
        bookmarkModel.setChapter(ReaderViewUtil.trim(title));
        Date date = new Date(bookmark.getCreatedAt().getTime());
        bookmarkModel.setData(DateTimeUtil.formatDate(date, DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM));
        String content = bookmark.getQuote();
        if(StringUtils.isNullOrEmpty(content)){
            content = JDReadApplication.getInstance().getString(R.string.security_none);
        }
        bookmarkModel.setContent(JDReadApplication.getInstance().getApplicationContext().getString(R.string.reader_content) + content);

        float progress = ReaderPageInfoFormat.calculateReadingProgress(bookmark.getPageNumber(),totalPage);
        bookmarkModel.setReadProgress(JDReadApplication.getInstance().getApplicationContext().getString(R.string.reader_read_progress) + progress + "%");
        bookmarkModel.setPosition(bookmark.getPosition());
        return bookmarkModel;
    }


    private static ReaderDocumentTableOfContentEntry locateEntry(List<ReaderDocumentTableOfContentEntry> entries, int pagePosition) {
        for (int i = 0; i < entries.size() - 1; i++) {
            ReaderDocumentTableOfContentEntry current = entries.get(i);
            int currentPagePosition = PagePositionUtils.getPosition(current.getPosition());
            int nextPagePosition = PagePositionUtils.getPosition(entries.get(i + 1).getPosition());
            if (currentPagePosition <= pagePosition && pagePosition < nextPagePosition) {
                return locateEntryWithChildren(current, pagePosition);
            }
        }

        int startEntryPosition = getDocumentTableOfContentEntryPosition(entries, 0);
        ReaderDocumentTableOfContentEntry current = entries.get(pagePosition < startEntryPosition ? 0 : entries.size() - 1);
        return locateEntryWithChildren(current, pagePosition);
    }

    private static int getDocumentTableOfContentEntryPosition(final List<ReaderDocumentTableOfContentEntry> entries, final int index) {
        ReaderDocumentTableOfContentEntry entry = entries.get(index);
        return PagePositionUtils.getPosition(entry.getPosition());
    }

    private static ReaderDocumentTableOfContentEntry locateEntryWithChildren(ReaderDocumentTableOfContentEntry entry, int pagePosition) {
        int currentPagePosition = PagePositionUtils.getPosition(entry.getPosition());
        if (!hasChildren(entry)) {
            return entry;
        }
        int firstChildPagePosition = PagePositionUtils.getPosition(entry.getChildren().get(0).getPosition());
        if (currentPagePosition <= pagePosition && pagePosition < firstChildPagePosition) {
            return entry;
        }
        return locateEntry(entry.getChildren(), pagePosition);
    }

    private static boolean hasChildren(ReaderDocumentTableOfContentEntry entry) {
        return entry.getChildren() != null && entry.getChildren().size() > 0;
    }
}
