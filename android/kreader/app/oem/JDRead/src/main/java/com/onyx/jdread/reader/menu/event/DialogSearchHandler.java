package com.onyx.jdread.reader.menu.event;

import com.onyx.jdread.reader.dialog.DialogSearchViewCallBack;
import com.onyx.jdread.reader.dialog.ViewCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by huxiaomao on 2018/2/1.
 */

public class DialogSearchHandler {
    private DialogSearchViewCallBack viewCallBack;
    private EventBus eventBus;

    public DialogSearchHandler(DialogSearchViewCallBack viewCallBack, EventBus eventBus) {
        this.viewCallBack = viewCallBack;
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    @Subscribe
    public void onSearchBackClickEvent(DialogSearchBackClickEvent event) {
        viewCallBack.getContent().dismiss();
    }

    public void registerListener() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
    }

    public void unregisterListener() {
        if (getEventBus().isRegistered(this)) {
            getEventBus().unregister(this);
        }
    }

    @Subscribe
    public void onPreIconClickEvent(PreIconClickEvent event) {
//        pageRecyclerView.prevPage();
    }

    @Subscribe
    public void onNextIconClickEvent(NextIconClickEvent event) {
        //if (pageRecyclerView.getPaginator().hasNextPage()){
//                    pageRecyclerView.nextPage();
//                }else {
//                    nextSearch();
//                }
    }

    @Subscribe
    public void onCloseSearchClickEvent(CloseSearchClickEvent event){
//        stopSearch();
    }

    @Subscribe
    public void onDeleteHistoryClickEvent(DeleteHistoryClickEvent event){
        //                new ToggleSearchHistoryAction("", false).execute(readerDataHolder, null);
//                dialogSearchModel.setSearchHistory(false);
    }

    @Subscribe
    public void onCloseHistoryClickEvent(CloseHistoryClickEvent event){
//        dialogSearchModel.setSearchHistory(false);
//        hideSoftInputWindow();
    }

    @Subscribe
    public void onSearchBackClickEvent(SearchBackClickEvent event){
        viewCallBack.searchBack();
    }

    @Subscribe
    public void onSearchPrevClickEvent(SearchPrevClickEvent event){
        viewCallBack.preSearchResult();
    }

    @Subscribe
    public void onSearchNextClickEvent(SearchNextClickEvent event){
        viewCallBack.nextSearchResult();
    }

    @Subscribe
    public void onSearchCloseClickEvent(SearchCloseClickEvent event){

    }

    @Subscribe
    public void onSearchImageClickEvent(SearchImageClickEvent event){
        viewCallBack.searchData();
    }
}
