package com.onyx.jdread.reader.menu.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;

import com.onyx.jdread.R;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.CloseDocumentEvent;
import com.onyx.jdread.reader.menu.event.BuyBookClickEvent;
import com.onyx.jdread.reader.menu.event.CloseReaderSettingMenuEvent;
import com.onyx.jdread.reader.menu.event.SearchContentEvent;
import com.onyx.jdread.reader.menu.event.ToggleBookmarkEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 17-12-27.
 */

public class ReaderTitleBarModel extends BaseObservable {
    private ObservableBoolean isBuy = new ObservableBoolean(true);
    private ObservableBoolean isSearchContext = new ObservableBoolean(true);
    private ObservableInt bookMarkImageId = new ObservableInt(R.mipmap.ic_read_bm_normal);
    private ObservableBoolean isShow = new ObservableBoolean(true);
    private EventBus eventBus;

    public ReaderTitleBarModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void backClick() {
        getEventBus().post(new CloseReaderSettingMenuEvent());
        getEventBus().post(new CloseDocumentEvent());
    }

    public void buyBookClick() {
        getEventBus().post(new BuyBookClickEvent());
    }

    public void searchContextClick() {
        getEventBus().post(new SearchContentEvent());
    }

    public void bookmarkCLick() {
        getEventBus().post(new ToggleBookmarkEvent());
    }

    public ObservableBoolean getIsBuy() {
        return isBuy;
    }

    public void setIsBuy(ObservableBoolean isBuy) {
        this.isBuy = isBuy;
    }

    public ObservableBoolean getIsSearchContext() {
        return isSearchContext;
    }

    public void setIsSearchContext(ObservableBoolean isSearchContext) {
        this.isSearchContext = isSearchContext;
    }

    public ObservableInt getBookMarkImageId() {
        return bookMarkImageId;
    }

    public void setBookMarkImageId(boolean isBookMark) {
        if(isBookMark){
            this.bookMarkImageId.set(R.mipmap.ic_read_bm_add);
        }else {
            this.bookMarkImageId.set(R.mipmap.ic_read_bm_normal);
        }
    }

    public ObservableBoolean getIsShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow.set(isShow);
    }
}
