package com.onyx.jdread.main.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

import com.onyx.jdread.main.event.TitleBarRightTitleEvent;
import com.onyx.jdread.reader.catalog.event.ExportReadNoteEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 17-12-27.
 */

public class TitleBarModel extends BaseObservable {
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> rightTitle = new ObservableField<>();

    public final ObservableField<Object> backEvent = new ObservableField<>();

    public EventBus eventBus;

    public TitleBarModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void back() {
        eventBus.post(backEvent.get());
    }

    public void rightTitleClicked(){
        eventBus.post(new TitleBarRightTitleEvent());
    }

    public void exportNoteClicked() {
        eventBus.post(new ExportReadNoteEvent());
    }
}
