package com.onyx.jdread.library.model;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.onyx.jdread.library.event.BackToLibraryFragmentEvent;
import com.onyx.jdread.library.event.SearchBookEvent;
import com.onyx.jdread.library.event.SearchBookKeyEvent;
import com.onyx.jdread.library.event.SubmitSearchBookEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Observable;

/**
 * Created by hehai on 18-1-17.
 */

public class SearchBookModel extends Observable {
    public final ObservableList<String> searchHistory = new ObservableArrayList<>();
    public final ObservableField<Object> backEvent = new ObservableField<>();
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
}
