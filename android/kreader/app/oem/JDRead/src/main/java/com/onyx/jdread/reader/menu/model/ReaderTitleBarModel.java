package com.onyx.jdread.reader.menu.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;

import com.onyx.jdread.R;
import com.onyx.jdread.reader.event.CloseDocumentEvent;
import com.onyx.jdread.reader.menu.event.BuyBookClickEvent;
import com.onyx.jdread.reader.menu.event.CloseReaderSettingMenuEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 17-12-27.
 */

public class ReaderTitleBarModel extends BaseObservable {
    private ObservableBoolean isBuy = new ObservableBoolean(true);
    private ObservableBoolean isSearchContext = new ObservableBoolean(true);
    private ObservableInt bookMarkImageId = new ObservableInt(R.mipmap.ic_read_bm_normal);

    public ReaderTitleBarModel() {

    }

    public void backClick() {
        EventBus.getDefault().post(new CloseReaderSettingMenuEvent());
        EventBus.getDefault().post(new CloseDocumentEvent());
    }

    public void buyBookClick() {
        EventBus.getDefault().post(new BuyBookClickEvent());
    }

    public void searchContextClick() {

    }

    public void bookmarkCLick() {

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

    public void setBookMarkImageId(ObservableInt bookMarkImageId) {
        this.bookMarkImageId = bookMarkImageId;
    }
}
