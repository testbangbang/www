package com.onyx.jdread.library.model;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.SearchHistory;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.library.action.ClearSearchHistoryAction;
import com.onyx.jdread.library.event.BackToLibraryFragmentEvent;
import com.onyx.jdread.library.event.ClearSearchHistoryEvent;
import com.onyx.jdread.library.event.SubmitSearchBookEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by hehai on 18-1-17.
 */

public class SearchBookModel extends Observable {
    public final ObservableField<String> searchKey = new ObservableField<>();
    public final ObservableList<SearchHistory> searchHistory = new ObservableArrayList<>();
    public final ObservableField<Object> backEvent = new ObservableField<>();
    public final ObservableList<DataModel> searchHint = new ObservableArrayList<>();
    public final ObservableList<DataModel> searchResult = new ObservableArrayList<>();
    public final ObservableBoolean isInputting = new ObservableBoolean(false);
    public final ObservableList<String> hotWords = new ObservableArrayList<>();
    public final ObservableField<String> defaultHotWord = new ObservableField<>();
    public EventBus eventBus;

    public SearchBookModel(EventBus eventBus) {
        this.eventBus = eventBus;
        backEvent.set(new BackToLibraryFragmentEvent());
    }

    public void back() {
        eventBus.post(backEvent.get());
    }

    public void clearHistory() {
        eventBus.post(new ClearSearchHistoryEvent());
    }

    public void search() {
        eventBus.post(new SubmitSearchBookEvent());
    }

    public boolean showHintList() {
        return isInputting.get() && StringUtils.isNotBlank(searchKey.get());
    }

    public boolean showResult() {
        return !isInputting.get() && StringUtils.isNotBlank(searchKey.get()) && searchResult.size() != 0;
    }

    public boolean showEmptyResult() {
        return !isInputting.get() && StringUtils.isNotBlank(searchKey.get()) && searchResult.size() == 0;
    }

    public void reAddHotWords(List<String> list) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        hotWords.clear();
        hotWords.addAll(list);
    }

    public List<String> getHotWords() {
        if (hotWords.size() <= 0) {
            return new ArrayList<>();
        }
        return hotWords.subList(0, hotWords.size());
    }

    public String getDefaultHotWord() {
        return defaultHotWord.get();
    }

    public void setDefaultHotWord(String word) {
        defaultHotWord.set(word);
    }
}
