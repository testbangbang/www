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
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.reader.catalog.adapter.BookmarkAdapter;
import com.onyx.jdread.reader.catalog.adapter.NoteAdapter;
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
    private ObservableField<String> noneString = new ObservableField<>();
    private ObservableInt currentTab = new ObservableInt(ReaderBookInfoDialogConfig.CATALOG_MODE);
    public final ObservableList<BookmarkModel> bookmarks = new ObservableArrayList<>();
    public final ObservableList<NoteModel> notes = new ObservableArrayList<>();
    public ObservableBoolean isEmpty = new ObservableBoolean(false);
    public ArrayList<TreeRecyclerView.TreeNode> rootNodes;
    private EventBus eventBus;
    private int bookmarksTotalPage = 0;
    private int notesTotalPage = 0;

    public int getBookmarksTotalPage() {
        return bookmarksTotalPage;
    }

    public void setBookmarksTotalPage(int totalPage) {
        this.bookmarksTotalPage = totalPage / BookmarkAdapter.row;
        if(totalPage % NoteAdapter.row != 0){
            this.bookmarksTotalPage += 1;
        }
    }

    public int getNotesTotalPage() {
        return notesTotalPage;
    }

    public void setNotesTotalPage(int totalPage) {
        this.notesTotalPage = totalPage / NoteAdapter.row;
        if(totalPage % NoteAdapter.row != 0){
            this.notesTotalPage += 1;
        }
    }

    public ReaderBookInfoModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public ObservableField<String> getNoneString() {
        return noneString;
    }

    public void setNoneString(String noneString) {
        this.noneString.set(noneString);
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
        setBookmarksTotalPage(this.bookmarks.size());
    }

    public int setNotes(List<Annotation> annotationList) {
        String content = ResManager.getString(R.string.reader_content);
        String note = ResManager.getString(R.string.reader_note);
        String none = ResManager.getString(R.string.security_none);
        this.notes.clear();
        for (Annotation annotation : annotationList) {
            NoteModel noteModel = new NoteModel();
            noteModel.setChapter(ReaderViewUtil.trim(annotation.getChapterName()));
            if(StringUtils.isNullOrEmpty(annotation.getNote())){
                noteModel.setNote(note + none);
            }else {
                noteModel.setNote(note + annotation.getNote());
            }
            noteModel.setContent(content + annotation.getQuote());
            noteModel.setPosition(annotation.getPosition());
            Date date = new Date(annotation.getUpdatedAt().getTime());
            noteModel.setData(DateTimeUtil.formatDate(date, DateTimeUtil.DATE_FORMAT_YYYYMMDD_2));
            this.notes.add(noteModel);
        }
        setNotesTotalPage(this.notes.size());
        return this.notes.size();
    }

    public void onTabCatalogClick() {
        if (rootNodes != null && rootNodes.size() > 0) {
            setIsEmpty(false);
        } else {
            setIsEmpty(true);
            setNoneString(ResManager.getString(R.string.catalog_none));
        }
        setCurrentTab(ReaderBookInfoDialogConfig.CATALOG_MODE);
        getEventBus().post(new TabCatalogClickEvent());
    }

    public void onTabBookmarkClick() {
        if (bookmarks.size() > 0) {
            setIsEmpty(false);
        } else {
            setIsEmpty(true);
            setNoneString(ResManager.getString(R.string.bookmark_none));
        }
        setCurrentTab(ReaderBookInfoDialogConfig.BOOKMARK_MODE);
        getEventBus().post(new TabBookmarkClickEvent());
    }

    public void onTabNoteClick() {
        if (notes.size() > 0) {
            setIsEmpty(false);
        } else {
            setIsEmpty(true);
            setNoneString(ResManager.getString(R.string.note_none));
        }
        setCurrentTab(ReaderBookInfoDialogConfig.NOTE_MODE);
        getEventBus().post(new TabNoteClickEvent());
    }
}
