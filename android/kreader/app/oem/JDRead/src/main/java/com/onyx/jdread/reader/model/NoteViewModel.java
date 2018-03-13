package com.onyx.jdread.reader.model;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ActivityNoteBinding;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.reader.catalog.event.AnnotationItemClickEvent;
import com.onyx.jdread.reader.data.NoteInfo;
import com.onyx.jdread.reader.event.NoteBackEvent;
import com.onyx.jdread.reader.event.AddNoteEvent;
import com.onyx.jdread.reader.event.UpdateNoteEvent;
import com.onyx.jdread.reader.menu.common.ReaderConfig;
import com.onyx.jdread.reader.ui.view.PageTextView;
import com.onyx.jdread.reader.utils.ReaderViewUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class NoteViewModel {
    private ObservableField<String> srcNote = new ObservableField<>();
    private ObservableField<String> srcEditNote = new ObservableField<>();
    private ObservableField<String> srcPageNumber = new ObservableField<>();
    private ObservableField<String> newNote = new ObservableField<>();
    private ObservableField<String> newPageNumber = new ObservableField<>();
    private ObservableField<String> createNoteDate = new ObservableField<>();
    private ObservableBoolean isSrcNoteModify = new ObservableBoolean(false);
    private ObservableField<String> title = new ObservableField<>();
    private ObservableBoolean isEdit = new ObservableBoolean(true);
    private ObservableField<String> chapterName = new ObservableField<>();
    private String pagePosition;
    private EventBus eventBus;
    private boolean isCreateNote = false;
    private Annotation annotation;
    private ActivityNoteBinding binding;
    private NoteInfo noteInfo;

    public ObservableField<String> getSrcPageNumber() {
        return srcPageNumber;
    }

    public void setSrcPageNumber(String srcPageNumber) {
        this.srcPageNumber.set(srcPageNumber);
    }

    public ObservableField<String> getNewPageNumber() {
        return newPageNumber;
    }

    public void setNewPageNumber(String newPageNumber) {
        this.newPageNumber.set(newPageNumber);
    }

    public NoteViewModel(ActivityNoteBinding binding) {
        this.binding = binding;
        this.binding.tvSrcNote.setOnPagingListener(new PageTextView.OnPagingListener() {
            @Override
            public void onPageChange(int currentPage, int totalPage) {
                setSrcPageNumber(currentPage + "/" + totalPage);
            }
        });
        this.binding.tvNewNote.setOnPagingListener(new PageTextView.OnPagingListener() {
            @Override
            public void onPageChange(int currentPage, int totalPage) {
                setNewPageNumber(currentPage + "/" + totalPage);
            }
        });
    }

    public void setReaderDataHolder(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public String getPagePosition() {
        return pagePosition;
    }

    public void setPagePosition(String pagePosition) {
        this.pagePosition = pagePosition;
    }

    public void setNoteInfo(Context context, NoteInfo noteInfo, Annotation annotation) {
        if (noteInfo == null) {
            ToastUtil.showToast(context, R.string.login_resutl_params_error);
            getEventBus().post(new NoteBackEvent());
            return;
        }
        this.annotation = annotation;
        update(noteInfo);
    }

    private void update(NoteInfo noteInfo) {
        setSrcNote(noteInfo.srcNote);
        setSrcEditNote(noteInfo.srcNote);
        setChapterName(ReaderViewUtil.trim(noteInfo.chapterName));
        setCreateNoteDate(noteInfo.updateDate);
        setNewNote(noteInfo.newNote);
        setIsEdit(noteInfo.isCreate);
        isCreateNote = noteInfo.isCreate;
        setTitle(noteInfo.bookName);
        setIsSrcNoteModify(noteInfo.isSrcNoteModify);
        setPagePosition(noteInfo.pagePosition);
        this.noteInfo = noteInfo;
    }

    public ObservableField<String> getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName.set(chapterName);
    }

    public ObservableBoolean getIsSrcNoteModify() {
        return isSrcNoteModify;
    }

    public void setIsSrcNoteModify(boolean isSrcNoteModify) {
        this.isSrcNoteModify.set(isSrcNoteModify);
    }

    public ObservableField<String> getCreateNoteDate() {
        return createNoteDate;
    }

    public void setCreateNoteDate(String createNoteDate) {
        this.createNoteDate.set(createNoteDate);
    }

    public ObservableField<String> getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public ObservableBoolean getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(boolean isEdit) {
        if (isEdit) {
            binding.srcNote.setVisibility(View.VISIBLE);
            binding.srcNote.setEnabled(true);
            binding.newNote.setEnabled(true);
            binding.newNote.setVisibility(View.VISIBLE);
        } else {
            binding.srcNote.setEnabled(false);
            binding.srcNote.setVisibility(View.GONE);
            binding.newNote.setEnabled(false);
            binding.newNote.setVisibility(View.GONE);
        }
        this.isEdit.set(isEdit);
    }

    public void backClick() {
        getEventBus().post(new NoteBackEvent());
    }

    public void saveClick() {
        if (isCreateNote) {
            getEventBus().post(new AddNoteEvent(newNote.get(),srcNote.get(),getSrcNoteSate()));
        } else {
            getEventBus().post(new UpdateNoteEvent(annotation,newNote.get(),srcEditNote.get(),getSrcNoteSate()));
        }
    }

    public int getSrcNoteSate(){
        String note = srcEditNote.get();
        if(!noteInfo.isSrcNoteModify) {
            if (noteInfo.srcNote.equals(note)) {
                return ReaderConfig.QUOTE_STATE_NOT_CHANGED;
            }
        }
        return ReaderConfig.QUOTE_STATE_MODIFY;
    }

    public void editClick() {
        setIsEdit(true);
    }

    public ObservableField<String> getSrcNote() {
        return srcNote;
    }

    public void setSrcNote(String srcNote) {
        this.srcNote.set(srcNote);
    }

    public ObservableField<String> getSrcEditNote() {
        return srcEditNote;
    }

    public void setSrcEditNote(String srcEditNote) {
        this.srcEditNote.set(srcEditNote);
    }

    public ObservableField<String> getNewNote() {
        return newNote;
    }

    public void setNewNote(String newNote) {
        this.newNote.set(newNote);
    }

    public NoteInfo getNoteInfo() {
        NoteInfo noteInfo = new NoteInfo();
        noteInfo.srcNote = srcNote.get();
        noteInfo.newNote = newNote.get();
        noteInfo.updateDate = createNoteDate.get();
        noteInfo.chapterName = chapterName.get();
        return noteInfo;
    }

    public void gotoPagePosition(){
        getEventBus().post(new AnnotationItemClickEvent(annotation));
    }
}
