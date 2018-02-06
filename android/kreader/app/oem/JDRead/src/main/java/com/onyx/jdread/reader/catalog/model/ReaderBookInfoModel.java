package com.onyx.jdread.reader.catalog.model;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.ui.view.TreeRecyclerView;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.catalog.event.TabBookmarkClickEvent;
import com.onyx.jdread.reader.catalog.event.TabCatalogClickEvent;
import com.onyx.jdread.reader.catalog.event.TabNoteClickEvent;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.common.ReaderBookInfoDialogConfig;
import com.onyx.jdread.reader.utils.ReaderViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by huxiaomao on 2018/1/9.
 */

public class ReaderBookInfoModel {
    private ObservableField<String> pageInfo = new ObservableField<>();
    private ObservableInt currentTab = new ObservableInt(ReaderBookInfoDialogConfig.CATALOG_MODE);
    public final ObservableList<BookmarkModel> bookmarks = new ObservableArrayList<>();
    public final ObservableList<NoteModel> notes = new ObservableArrayList<>();
    public ObservableBoolean isEmpty = new ObservableBoolean(false);
    public ArrayList<TreeRecyclerView.TreeNode> rootNodes;
    private EventBus eventBus;

    public ReaderBookInfoModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void setRootNodes(ArrayList<TreeRecyclerView.TreeNode> rootNodes) {
        this.rootNodes = rootNodes;
        if (rootNodes.size() > 0) {
            setIsEmpty(false);
        } else {
            setIsEmpty(true);
        }
    }

    public ObservableBoolean getIsEmpty() {
        return isEmpty;
    }

    public void setIsEmpty(boolean isEmpty) {
        this.isEmpty.set(isEmpty);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public ObservableInt getCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(int tabModel) {
        this.currentTab.set(tabModel);
    }

    public ObservableField<String> getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(String pageInfo) {
        this.pageInfo.set(pageInfo);
    }

    public ObservableList<BookmarkModel> getBookmarks() {
        return bookmarks;
    }

    public ObservableList<NoteModel> getNotes() {
        return notes;
    }

    public void setBookmarks(ReaderDocumentTableOfContent readerDocumentTableOfContent, List<Bookmark> bookmarks, int totalPage) {
        for (Bookmark bookmark : bookmarks) {
            this.bookmarks.add(BookmarkModel.convertObject(readerDocumentTableOfContent, bookmark, totalPage));
        }
    }

    public void setNotes(Context context,List<Annotation> annotationList) {
        String content = context.getResources().getString(R.string.reader_content);
        for (Annotation annotation : annotationList) {
            NoteModel noteModel = new NoteModel();
            noteModel.setChapter(ReaderViewUtil.trim(annotation.getChapterName()));
            noteModel.setNote(annotation.getNote());
            noteModel.setContent(content + annotation.getQuote());
            noteModel.setPosition(annotation.getPosition());
            Date date = new Date(annotation.getCreatedAt().getTime());
            noteModel.setData(DateTimeUtil.formatDate(date, DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM));
            this.notes.add(noteModel);
        }
    }

    public void onTabCatalogClick() {
        setCurrentTab(ReaderBookInfoDialogConfig.CATALOG_MODE);
        getEventBus().post(new TabCatalogClickEvent());
        if (rootNodes.size() > 0) {
            setIsEmpty(false);
        } else {
            setIsEmpty(true);
        }
    }

    public void onTabBookmarkClick() {
        setCurrentTab(ReaderBookInfoDialogConfig.BOOKMARK_MODE);
        getEventBus().post(new TabBookmarkClickEvent());
        if (bookmarks.size() > 0) {
            setIsEmpty(false);
        } else {
            setIsEmpty(true);
        }
    }

    public void onTabNoteClick() {
        setCurrentTab(ReaderBookInfoDialogConfig.NOTE_MODE);
        getEventBus().post(new TabNoteClickEvent());
        if (notes.size() > 0) {
            setIsEmpty(false);
        } else {
            setIsEmpty(true);
        }
    }
}
