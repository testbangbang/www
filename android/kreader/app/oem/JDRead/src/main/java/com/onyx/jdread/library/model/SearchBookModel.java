package com.onyx.jdread.library.model;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.library.event.BackToLibraryFragmentEvent;
import com.onyx.jdread.library.event.SubmitSearchBookEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Observable;

/**
 * Created by hehai on 18-1-17.
 */

public class SearchBookModel extends Observable {
    public final ObservableField<String> searchKey = new ObservableField<>();
    public final ObservableList<String> searchHistory = new ObservableArrayList<>();
    public final ObservableField<Object> backEvent = new ObservableField<>();
    public final ObservableList<DataModel> searchHint = new ObservableArrayList<>();
    public final ObservableList<DataModel> searchResult = new ObservableArrayList<>();
    public final ObservableBoolean isInputting = new ObservableBoolean(false);
    public EventBus eventBus;

    public SearchBookModel(EventBus eventBus) {
        this.eventBus = eventBus;
        backEvent.set(new BackToLibraryFragmentEvent());
    }

    public void back() {
        eventBus.post(backEvent.get());
    }

    public void clearHistory() {
        searchHistory.clear();
    }

    public void search() {
        eventBus.post(new SubmitSearchBookEvent());
    }

    public boolean showHintList() {
        return isInputting.get() && StringUtils.isNotBlank(searchKey.get());
    }

    public boolean showResult() {
        return !isInputting.get() && StringUtils.isNotBlank(searchKey.get());
    }
}
