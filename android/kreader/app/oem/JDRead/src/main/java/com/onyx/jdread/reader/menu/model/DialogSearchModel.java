package com.onyx.jdread.reader.menu.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.onyx.jdread.reader.menu.event.CloseHistoryClickEvent;
import com.onyx.jdread.reader.menu.event.CloseSearchClickEvent;
import com.onyx.jdread.reader.menu.event.DeleteHistoryClickEvent;
import com.onyx.jdread.reader.menu.event.DeleteInputWordEvent;
import com.onyx.jdread.reader.menu.event.DialogSearchBackClickEvent;
import com.onyx.jdread.reader.menu.event.NextIconClickEvent;
import com.onyx.jdread.reader.menu.event.PreIconClickEvent;
import com.onyx.jdread.reader.menu.event.SearchBackClickEvent;
import com.onyx.jdread.reader.menu.event.SearchCloseClickEvent;
import com.onyx.jdread.reader.menu.event.SearchImageClickEvent;
import com.onyx.jdread.reader.menu.event.SearchNextClickEvent;
import com.onyx.jdread.reader.menu.event.SearchPrevClickEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/2/1.
 */

public class DialogSearchModel {
    private EventBus eventBus;
    private ObservableField<String> totalPage = new ObservableField<>();
    private ObservableField<String> pageIndicator = new ObservableField<>();
    private ObservableField<String> textViewMessage = new ObservableField<>();
    private ObservableBoolean searchContent = new ObservableBoolean(true);
    private ObservableBoolean loadingLayout = new ObservableBoolean(false);
    private ObservableBoolean searchHistory = new ObservableBoolean(false);
    private ObservableBoolean searchInputLayout = new ObservableBoolean(true);
    private ObservableBoolean floatToolBar = new ObservableBoolean(false);
    private ObservableBoolean dividerLine = new ObservableBoolean(false);
    private ObservableBoolean deleteInputWord = new ObservableBoolean(false);
    private ObservableBoolean totalPageShow = new ObservableBoolean(false);

    public ObservableBoolean getTotalPageShow() {
        return totalPageShow;
    }

    public void setTotalPageShow(boolean totalPageShow) {
        this.totalPageShow.set(totalPageShow);
    }

    public ObservableBoolean getDeleteInputWord() {
        return deleteInputWord;
    }

    public void setDeleteInputWord(boolean deleteInputWord) {
        this.deleteInputWord.set(deleteInputWord);
    }

    public ObservableBoolean getDividerLine() {
        return dividerLine;
    }

    public void setDividerLine(boolean dividerLine) {
        this.dividerLine.set(dividerLine);
    }

    public ObservableBoolean getFloatToolBar() {
        return floatToolBar;
    }

    public void setFloatToolBar(boolean floatToolBar) {
        this.floatToolBar.set(floatToolBar);
    }

    public ObservableBoolean getSearchInputLayout() {
        return searchInputLayout;
    }

    public void setSearchInputLayout(boolean searchInputLayout) {
        this.searchInputLayout.set(searchInputLayout);
    }

    public ObservableBoolean getSearchHistory() {
        return searchHistory;
    }

    public void setSearchHistory(boolean searchHistory) {
        this.searchHistory.set(searchHistory);
    }

    public ObservableBoolean getLoadingLayout() {
        return loadingLayout;
    }

    public void setLoadingLayout(boolean loadingLayout) {
        this.loadingLayout.set(loadingLayout);
    }

    public ObservableBoolean getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(boolean searchContent) {
        this.searchContent.set(searchContent);
    }

    public ObservableField<String> getTextViewMessage() {
        return textViewMessage;
    }

    public void setTextViewMessage(String textViewMessage) {
        this.textViewMessage.set(textViewMessage);
    }

    public ObservableField<String> getPageIndicator() {
        return pageIndicator;
    }

    public void setPageIndicator(String pageIndicator) {
        this.pageIndicator.set(pageIndicator);
    }

    public ObservableField<String> getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage.set(totalPage);
    }

    public DialogSearchModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void backClick() {
        eventBus.post(new DialogSearchBackClickEvent());
    }

    public void preIconClick() {
        eventBus.post(new PreIconClickEvent());
    }

    public void nextIconClick() {
        eventBus.post(new NextIconClickEvent());
    }

    public void closeSearchClick() {
        eventBus.post(new CloseSearchClickEvent());
    }

    public void deleteHistoryClick() {
        eventBus.post(new DeleteHistoryClickEvent());
    }

    public void closeHistoryClick() {
        eventBus.post(new CloseHistoryClickEvent());
    }

    public void searchBackClick() {
        eventBus.post(new SearchBackClickEvent());
    }

    public void searchPrevClick() {
        eventBus.post(new SearchPrevClickEvent());
    }

    public void searchNextClick() {
        eventBus.post(new SearchNextClickEvent());
    }

    public void searchCloseClick() {
        eventBus.post(new SearchCloseClickEvent());
    }

    public void searchImageClick() {
        eventBus.post(new SearchImageClickEvent());
    }

    public void deleteInputWordClick() {
        eventBus.post(new DeleteInputWordEvent());
    }

    public void dismissZoneClick(){
        eventBus.post(new DialogSearchBackClickEvent());
    }
}
