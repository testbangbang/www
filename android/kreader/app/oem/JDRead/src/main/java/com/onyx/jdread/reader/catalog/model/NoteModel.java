package com.onyx.jdread.reader.catalog.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

/**
 * Created by huxiaomao on 2018/1/10.
 */

public class NoteModel extends BaseObservable {
    private ObservableField<String> chapter = new ObservableField<>();
    private ObservableField<String> data = new ObservableField<>();
    private ObservableField<String> note = new ObservableField<>();
    private ObservableField<String> content = new ObservableField<>();

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

    public ObservableField<String> getNote() {
        return note;
    }

    public void setNote(String readProgress) {
        this.note.set(readProgress);
    }
}
