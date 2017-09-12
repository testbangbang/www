package com.onyx.android.dr.activity;

import com.onyx.android.dr.data.MenuBean;
import com.onyx.android.sdk.data.model.Library;

import java.util.List;

/**
 * Created by hehai on 17-6-28.
 */

public interface MainView {
    void setTabMenuData(List<MenuBean> menuData);
}
