package com.onyx.android.dr.interfaces;

import com.onyx.android.dr.data.MenuData;

import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public interface MyNotesView {
    void setMyracksData(List<MenuData> menuDatas);
    void setMyThinkData(List<MenuData> menuDatas);
    void setMyCreationData(List<MenuData> menuDatas);
}
