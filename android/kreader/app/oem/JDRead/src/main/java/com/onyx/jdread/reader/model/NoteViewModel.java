package com.onyx.jdread.reader.model;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.reader.data.NoteInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.NoteBackEvent;
import com.onyx.jdread.reader.event.SaveNoteEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class NoteViewModel {
    private ObservableField<String> srcNote = new ObservableField<>();
    private ObservableField<String> newNote = new ObservableField<>();
    private ObservableField<String> createNoteDate = new ObservableField<>();
    private ObservableBoolean isSrcNoteModify = new ObservableBoolean(false);
    private ObservableField<String> title = new ObservableField<>();
    private ObservableBoolean isEdit = new ObservableBoolean(false);
    private ObservableInt saveIcon = new ObservableInt(R.mipmap.ic_read_edit);
    private ObservableField<String> chapterName = new ObservableField<>();
    private String pagePosition;
    private ReaderDataHolder readerDataHolder;

    public ReaderDataHolder getReaderDataHolder() {
        return readerDataHolder;
    }

    public void setReaderDataHolder(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    public String getPagePosition() {
        return pagePosition;
    }

    public void setPagePosition(String pagePosition) {
        this.pagePosition = pagePosition;
    }

    public void setNoteInfo(Context context, NoteInfo noteInfo) {
        if (noteInfo == null) {
            ToastUtil.showToast(context, R.string.login_resutl_params_error);
            EventBus.getDefault().post(new NoteBackEvent());
            return;
        }
        update(noteInfo);
    }

    private void update(NoteInfo noteInfo) {
        setSrcNote(noteInfo.srcNote);
        setChapterName(noteInfo.chapterName);
        setCreateNoteDate(noteInfo.createDate);
        setNewNote(noteInfo.newNote);
        setIsEdit(noteInfo.isCreate);
        setTitle(noteInfo.bookName);
        setIsSrcNoteModify(noteInfo.isSrcNoteModify);
        setPagePosition(noteInfo.pagePosition);
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

    public ObservableInt getSaveIcon() {
        return saveIcon;
    }

    public void setSaveIcon(int saveIcon) {
        this.saveIcon.set(saveIcon);
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
        this.isEdit.set(isEdit);
        if (isEdit) {
            setSaveIconSave();
        } else {
            setSaveIconEdit();
        }
    }

    private void setSaveIconEdit() {
        setSaveIcon(R.mipmap.ic_read_edit);
    }

    private void setSaveIconSave() {
        setSaveIcon(R.mipmap.ic_read_save);
    }

    public void backClick() {
        EventBus.getDefault().post(new NoteBackEvent());
    }

    public void saveClick() {
        if (isEdit.get()) {

            EventBus.getDefault().post(new SaveNoteEvent());
        } else {
            setIsEdit(true);
        }
    }

    public ObservableField<String> getSrcNote() {
        return srcNote;
    }

    public void setSrcNote(String srcNote) {
        this.srcNote.set(srcNote);
    }

    public ObservableField<String> getNewNote() {
        return newNote;
    }

    public void setNewNote(String newNote) {
        this.newNote.set(newNote);
    }

    public NoteInfo getNoteInfo(){
        NoteInfo noteInfo = new NoteInfo();
        noteInfo.srcNote = srcNote.get();
        noteInfo.newNote = newNote.get();
        noteInfo.createDate = createNoteDate.get();
        return noteInfo;
    }
}
