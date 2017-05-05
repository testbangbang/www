package com.onyx.android.eschool.holder;

import android.content.Context;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.LibraryViewInfo;

/**
 * Created by suicheng on 2017/4/15.
 */

public class LibraryDataHolder extends BaseDataHolder {

    private DataManager dataManager = new DataManager();
    private LibraryViewInfo libraryViewInfo = new LibraryViewInfo();

    public LibraryDataHolder(Context context) {
        super(context);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public LibraryViewInfo getLibraryViewInfo() {
        return libraryViewInfo;
    }

    public void setLibraryViewInfo(LibraryViewInfo libraryViewInfo) {
        this.libraryViewInfo = libraryViewInfo;
    }
}
