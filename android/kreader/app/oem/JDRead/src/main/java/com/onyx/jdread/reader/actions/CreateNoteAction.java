package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.NoteInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.highlight.ReaderSelectionInfo;
import com.onyx.jdread.util.TimeUtils;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class CreateNoteAction extends BaseReaderAction {
    private NoteInfo noteInfo;

    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        String pagePosition = readerDataHolder.getCurrentPagePosition();
        ReaderSelectionInfo readerSelectionInfo = readerDataHolder.getReaderSelectionInfo().getReaderSelectionInfo(pagePosition);
        getNoteInfo(readerSelectionInfo, pagePosition, readerDataHolder.getBookName());
        if (baseCallback != null) {
            baseCallback.onNext(null);
        }
    }

    private NoteInfo getNoteInfo(ReaderSelectionInfo readerSelectionInfo, String pagePosition, String bookName) {
        noteInfo = new NoteInfo();
        noteInfo.isCreate = true;
        noteInfo.isSrcNoteModify = false;
        noteInfo.createDate = TimeUtils.getCurrentDataInString();
        noteInfo.pagePosition = pagePosition;
        noteInfo.newNote = "";
        noteInfo.srcNote = readerSelectionInfo.getCurrentSelection().getText();
        noteInfo.chapterName = readerSelectionInfo.pageInfo.getName();
        noteInfo.bookName = bookName;
        return noteInfo;
    }

    public NoteInfo getNoteInfo() {
        return noteInfo;
    }
}
