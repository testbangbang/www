package com.onyx.jdread.reader.catalog.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.onyx.jdread.reader.catalog.event.ExportReadNoteEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/3/17.
 */

public class BookTitleModel extends BaseObservable {
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableBoolean isShowExport = new ObservableBoolean(false);

    public final ObservableField<Object> backEvent = new ObservableField<>();

    public EventBus eventBus;

    public BookTitleModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void back() {
        eventBus.post(backEvent.get());
    }

    public void exportNoteClicked() {
        eventBus.post(new ExportReadNoteEvent());
    }

    public ObservableBoolean getIsShowExport() {
        return isShowExport;
    }

    public void setIsShowExport(boolean isShowExport){
        this.isShowExport.set(isShowExport);
    }
}
