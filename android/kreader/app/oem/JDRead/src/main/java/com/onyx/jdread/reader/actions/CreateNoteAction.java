package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.jdread.reader.data.NoteInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.highlight.SelectionInfo;
import com.onyx.jdread.util.TimeUtils;

import java.util.Date;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class CreateNoteAction extends BaseReaderAction {
    private NoteInfo noteInfo;
    private Annotation annotation;

    public CreateNoteAction(Annotation annotation) {
        this.annotation = annotation;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        if(annotation != null){
            getEditNoteInfo(readerDataHolder);
        }else {
            String pagePosition = readerDataHolder.getCurrentPagePosition();
            SelectionInfo readerSelectionInfo = readerDataHolder.getReaderSelectionInfo().getReaderSelectionInfo(pagePosition);
            getNoteInfo(readerSelectionInfo, pagePosition, readerDataHolder.getBookName(), readerDataHolder.getReaderViewInfo().chapterName);
        }
        if (baseCallback != null) {
            baseCallback.onNext(null);
        }
    }

    private NoteInfo getNoteInfo(SelectionInfo readerSelectionInfo, String pagePosition, String bookName,String chapterName) {
        noteInfo = new NoteInfo();
        noteInfo.isCreate = true;
        noteInfo.isSrcNoteModify = false;
        noteInfo.updateDate = TimeUtils.getCurrentDataInString();
        noteInfo.pagePosition = pagePosition;
        noteInfo.newNote = "";
        noteInfo.srcNote = readerSelectionInfo.getCurrentSelection().getText();
        noteInfo.chapterName = chapterName;
        noteInfo.bookName = bookName;
        return noteInfo;
    }

    private NoteInfo getEditNoteInfo(ReaderDataHolder readerDataHolder) {
        noteInfo = new NoteInfo();
        noteInfo.isCreate = false;
        noteInfo.isSrcNoteModify = true;
        Date date = new Date(annotation.getUpdatedAt().getTime());
        noteInfo.updateDate = DateTimeUtil.formatDate(date, DateTimeUtil.DATE_FORMAT_YYYYMMDD_2);
        noteInfo.pagePosition = annotation.getPosition();
        noteInfo.newNote = annotation.getNote();
        noteInfo.srcNote = annotation.getQuote();
        noteInfo.chapterName = annotation.getChapterName();
        noteInfo.bookName = readerDataHolder.getBookName();
        return noteInfo;
    }

    public NoteInfo getNoteInfo() {
        return noteInfo;
    }

    public Annotation getAnnotation() {
        return annotation;
    }
}
