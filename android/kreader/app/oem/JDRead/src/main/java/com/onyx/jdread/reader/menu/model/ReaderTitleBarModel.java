package com.onyx.jdread.reader.menu.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;

import com.onyx.jdread.R;

/**
 * Created by hehai on 17-12-27.
 */

public class ReaderTitleBarModel extends BaseObservable {
    private ObservableBoolean isBuy = new ObservableBoolean(false);
    private ObservableBoolean isSearchContext = new ObservableBoolean(false);
    private ObservableInt bookMarkImageId = new ObservableInt(R.mipmap.ic_read_bm_normal);

    public ReaderTitleBarModel() {

    }

    public void back() {

    }

    public void buyBookClick() {

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
