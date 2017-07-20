package com.onyx.android.dr.interfaces;

import com.onyx.android.dr.data.MenuBean;

import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public interface MyNotesView {
    void setMyracksData(List<MenuBean> menuDatas);
    void setMyThinkData(List<MenuBean> menuDatas);
    void setMyCreationData(List<MenuBean> menuDatas);
}
